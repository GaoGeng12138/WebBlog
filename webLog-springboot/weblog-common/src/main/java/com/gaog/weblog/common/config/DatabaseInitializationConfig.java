package com.gaog.weblog.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
@Slf4j
public class DatabaseInitializationConfig {
    private static final String SQL_RESOURCE_PATTERN = "classpath*:sql/*.sql";
    private static final Pattern INSERT_STATEMENT_PATTERN =
            Pattern.compile("(?i)^INSERT\\s+(?:IGNORE\\s+)?INTO\\b");
    private static final Pattern CREATE_TABLE_PATTERN =
            Pattern.compile("(?i)^CREATE\\s+TABLE\\s+(?:IF\\s+NOT\\s+EXISTS\\s+)?`?(\\w+)`?");
    private static final Pattern ALTER_ADD_COLUMN_PATTERN =
            Pattern.compile("(?i)^ALTER\\s+TABLE\\s+`?(\\w+)`?\\s+ADD\\s+COLUMN\\s+`?(\\w+)`?");
    private static final Pattern ALTER_DROP_COLUMN_PATTERN =
            Pattern.compile("(?i)^ALTER\\s+TABLE\\s+`?(\\w+)`?\\s+DROP\\s+COLUMN\\s+`?(\\w+)`?");

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${app.database.init.create:true}")
    private boolean databaseInitEnabled;

    @Value("${app.database.init.inserts:true}")
    private boolean executeInserts;

    public DatabaseInitializationConfig(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @PostConstruct
    public void init() {
        log.info("Database initialization config loaded");
    }

    /**
     * Execute database initialization after application is ready.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeDatabase() {
        if (!databaseInitEnabled) {
            log.info("Database initialization is disabled");
            return;
        }

        log.info("Starting database initialization check...");

        try {
            List<String> createStatements = new ArrayList<>();
            List<String> migrationStatements = new ArrayList<>();
            List<String> insertStatements = new ArrayList<>();

            for (String sql : loadSqlStatements()) {
                String trimmedSql = sql.trim();
                if (trimmedSql.isEmpty()) {
                    continue;
                }

                if (isCreateTableStatement(trimmedSql)) {
                    createStatements.add(trimmedSql);
                } else if (isInsertStatement(trimmedSql)) {
                    insertStatements.add(trimmedSql);
                } else {
                    migrationStatements.add(trimmedSql);
                }
            }

            executeCreateStatements(createStatements);
            executeMigrationStatements(migrationStatements);

            if (executeInserts) {
                executeInsertStatements(insertStatements);
            } else {
                log.info("INSERT statement execution is disabled");
            }

            log.info("Database initialization check completed.");
        } catch (Exception e) {
            log.error("Error during database initialization", e);
        }
    }

    /**
     * Load all SQL statements from the classpath sql directory.
     *
     * @return all executable SQL statements sorted by file name
     * @throws Exception when reading any SQL resource fails
     */
    private List<String> loadSqlStatements() throws Exception {
        List<String> sqlStatements = new ArrayList<>();
        Resource[] resources = resourcePatternResolver.getResources(SQL_RESOURCE_PATTERN);
        Arrays.sort(resources, Comparator.comparing(Resource::getFilename, Comparator.nullsLast(String::compareToIgnoreCase)));

        for (Resource resource : resources) {
            sqlStatements.addAll(parseSqlResource(resource));
        }

        return sqlStatements;
    }

    /**
     * Parse a SQL resource into executable statements.
     *
     * @param resource SQL resource to parse
     * @return parsed SQL statements
     * @throws Exception when reading the resource fails
     */
    private List<String> parseSqlResource(Resource resource) throws Exception {
        List<String> sqlStatements = new ArrayList<>();

        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            StringBuilder currentStatement = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                    continue;
                }

                currentStatement.append(line).append("\n");
                if (trimmedLine.endsWith(";")) {
                    String statement = currentStatement.toString().trim();
                    if (!statement.isEmpty()) {
                        sqlStatements.add(statement);
                    }
                    currentStatement = new StringBuilder();
                }
            }
        }

        return sqlStatements;
    }

    /**
     * Execute CREATE TABLE statements only when the target table does not exist.
     *
     * @param createStatements create statements
     */
    private void executeCreateStatements(List<String> createStatements) {
        for (String createSql : createStatements) {
            String tableName = extractTableName(createSql);
            if (tableName != null && isTableExists(tableName)) {
                log.info("Table {} already exists, skipping creation", tableName);
                continue;
            }

            try {
                jdbcTemplate.execute(createSql);
                if (tableName != null) {
                    log.info("Successfully created table: {}", tableName);
                }
            } catch (Exception e) {
                log.error("Failed to create table: {}", tableName, e);
            }
        }
    }

    /**
     * Execute migration statements in a safe, idempotent way.
     *
     * @param migrationStatements migration statements
     */
    private void executeMigrationStatements(List<String> migrationStatements) {
        for (String migrationSql : migrationStatements) {
            if (shouldSkipAddColumnMigration(migrationSql) || shouldSkipDropColumnMigration(migrationSql)) {
                continue;
            }

            try {
                jdbcTemplate.execute(migrationSql);
                log.info("Successfully executed migration statement");
            } catch (Exception e) {
                log.error("Failed to execute migration statement: {}", migrationSql, e);
            }
        }
    }

    /**
     * Execute insert statements as idempotent seed data.
     *
     * @param insertStatements insert statements
     */
    private void executeInsertStatements(List<String> insertStatements) {
        for (String insertSql : insertStatements) {
            String idempotentInsertSql = normalizeInsertStatement(insertSql);
            try {
                jdbcTemplate.execute(idempotentInsertSql);
                log.info("Successfully executed insert statement");
            } catch (Exception e) {
                log.warn("Failed to execute insert statement (may be duplicate): {}", e.getMessage());
            }
        }
    }

    /**
     * Extract table name from a CREATE TABLE statement.
     *
     * @param createTableSql create table SQL
     * @return table name or null when parsing fails
     */
    private String extractTableName(String createTableSql) {
        Matcher matcher = CREATE_TABLE_PATTERN.matcher(createTableSql.trim());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Check if a table exists in the database.
     *
     * @param tableName table name
     * @return true when the table exists
     */
    private boolean isTableExists(String tableName) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String schema = null;
            String catalog = null;

            if (driverClassName.toLowerCase().contains("mysql") || driverClassName.contains("p6spy")) {
                catalog = connection.getCatalog();
            } else {
                schema = connection.getSchema();
            }

            try (ResultSet resultSet = metaData.getTables(catalog, schema, tableName, new String[]{"TABLE"})) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            log.error("Error checking if table exists: {}", tableName, e);
            return false;
        }
    }

    /**
     * Check if a column exists in a table.
     *
     * @param tableName table name
     * @param columnName column name
     * @return true when the column exists
     */
    private boolean isColumnExists(String tableName, String columnName) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String schema = null;
            String catalog = null;

            if (driverClassName.toLowerCase().contains("mysql") || driverClassName.contains("p6spy")) {
                catalog = connection.getCatalog();
            } else {
                schema = connection.getSchema();
            }

            try (ResultSet resultSet = metaData.getColumns(catalog, schema, tableName, columnName)) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            log.error("Error checking if column exists: {}.{}", tableName, columnName, e);
            return false;
        }
    }

    /**
     * Skip ADD COLUMN migrations if the target column already exists.
     *
     * @param migrationSql migration statement
     * @return true if the statement should be skipped
     */
    private boolean shouldSkipAddColumnMigration(String migrationSql) {
        Matcher matcher = ALTER_ADD_COLUMN_PATTERN.matcher(migrationSql.trim());
        if (!matcher.find()) {
            return false;
        }

        String tableName = matcher.group(1);
        String columnName = matcher.group(2);
        if (isColumnExists(tableName, columnName)) {
            log.info("Column {}.{} already exists, skipping migration", tableName, columnName);
            return true;
        }
        return false;
    }

    /**
     * Skip DROP COLUMN migrations if the target column does not exist.
     *
     * @param migrationSql migration statement
     * @return true if the statement should be skipped
     */
    private boolean shouldSkipDropColumnMigration(String migrationSql) {
        Matcher matcher = ALTER_DROP_COLUMN_PATTERN.matcher(migrationSql.trim());
        if (!matcher.find()) {
            return false;
        }

        String tableName = matcher.group(1);
        String columnName = matcher.group(2);
        if (!isColumnExists(tableName, columnName)) {
            log.info("Column {}.{} does not exist, skipping migration", tableName, columnName);
            return true;
        }
        return false;
    }

    /**
     * Determine whether the statement is a CREATE TABLE statement.
     *
     * @param sql sql statement
     * @return true if the statement creates a table
     */
    private boolean isCreateTableStatement(String sql) {
        return CREATE_TABLE_PATTERN.matcher(sql).find();
    }

    /**
     * Determine whether the statement is an INSERT statement.
     *
     * @param sql sql statement
     * @return true if the statement inserts data
     */
    private boolean isInsertStatement(String sql) {
        return INSERT_STATEMENT_PATTERN.matcher(sql).find();
    }

    /**
     * Normalize insert statements to be idempotent.
     *
     * @param insertSql insert statement
     * @return normalized insert statement
     */
    private String normalizeInsertStatement(String insertSql) {
        if (insertSql.matches("(?i)^INSERT\\s+IGNORE\\s+INTO\\b.*")) {
            return insertSql;
        }
        return insertSql.replaceFirst("(?i)^INSERT\\s+INTO", "INSERT IGNORE INTO");
    }
}
