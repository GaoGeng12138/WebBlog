package com.gaog.weblog.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
@Slf4j
public class DatabaseInitializationConfig {
    private static final Pattern INSERT_STATEMENT_PATTERN =
            Pattern.compile("(?i)^INSERT\\s+(?:IGNORE\\s+)?INTO\\b");
    private static final Pattern CREATE_TABLE_PATTERN =
            Pattern.compile("(?i)^CREATE\\s+TABLE\\s+(?:IF\\s+NOT\\s+EXISTS\\s+)?`?(\\w+)`?");
    private static final Pattern ALTER_ADD_COLUMN_PATTERN =
            Pattern.compile("(?i)^ALTER\\s+TABLE\\s+`?(\\w+)`?\\s+ADD\\s+COLUMN\\s+`?(\\w+)`?");
    private static final Pattern ALTER_DROP_COLUMN_PATTERN =
            Pattern.compile("(?i)^ALTER\\s+TABLE\\s+`?(\\w+)`?\\s+DROP\\s+COLUMN\\s+`?(\\w+)`?");

    private static final String CREATE_TABLE_SQL = "sql/CreateTable.sql";
    private static final String ADD_COLUMN_SQL = "sql/AddColumn.sql";
    private static final String UPDATE_DATA_SQL = "sql/UpdateData.sql";
    private static final String INSERT_DATA_SQL = "sql/InsertData.sql";

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

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
            executeCreateStatements(loadSqlStatements(CREATE_TABLE_SQL));
            executeUpdateStatements(loadSqlStatements(ADD_COLUMN_SQL));
            executeUpdateStatements(loadSqlStatements(UPDATE_DATA_SQL));

            if (executeInserts) {
                executeInsertStatements(loadSqlStatements(INSERT_DATA_SQL));
            } else {
                log.info("INSERT statement execution is disabled");
            }

            log.info("Database initialization check completed.");
        } catch (Exception e) {
            log.error("Error during database initialization", e);
        }
    }

    /**
     * Load SQL statements from one classpath resource.
     *
     * @param filePath sql file path under resources
     * @return parsed SQL statements
     * @throws Exception when reading the resource fails
     */
    private List<String> loadSqlStatements(String filePath) throws Exception {
        List<String> sqlStatements = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource(filePath);

        if (!resource.exists()) {
            log.info("SQL file {} does not exist, skipping", filePath);
            return sqlStatements;
        }

        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            StringBuilder currentStatement = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) {
                    continue;
                }

                currentStatement.append(line).append('\n');
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
     * Execute UPDATE or ALTER statements in a safe way.
     *
     * @param updateStatements update statements
     */
    private void executeUpdateStatements(List<String> updateStatements) {
        for (String updateSql : updateStatements) {
            if (shouldSkipAddColumnMigration(updateSql) || shouldSkipDropColumnMigration(updateSql)) {
                continue;
            }

            try {
                jdbcTemplate.execute(updateSql);
                log.info("Successfully executed update statement");
            } catch (Exception e) {
                log.error("Failed to execute update statement: {}", updateSql, e);
            }
        }
    }

    /**
     * Execute INSERT statements as idempotent seed data.
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
     * @param updateSql update statement
     * @return true if the statement should be skipped
     */
    private boolean shouldSkipAddColumnMigration(String updateSql) {
        Matcher matcher = ALTER_ADD_COLUMN_PATTERN.matcher(updateSql.trim());
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
     * @param updateSql update statement
     * @return true if the statement should be skipped
     */
    private boolean shouldSkipDropColumnMigration(String updateSql) {
        Matcher matcher = ALTER_DROP_COLUMN_PATTERN.matcher(updateSql.trim());
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
