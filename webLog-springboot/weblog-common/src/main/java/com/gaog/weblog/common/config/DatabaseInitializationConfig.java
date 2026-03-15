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

    private final DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

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
     * Execute database initialization after application is ready
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeDatabase() {
        if (!databaseInitEnabled) {
            log.info("Database initialization is disabled");
            return;
        }
        
        log.info("Starting database initialization check...");
        
        try {
            // First, process CREATE TABLE statements
            List<String> createTableStatements = parseSqlFile("sql/CreateTable.sql");
            
            for (String sql : createTableStatements) {
                String trimmedSql = sql.trim();
                if (trimmedSql.toUpperCase().startsWith("CREATE TABLE")) {
                    String tableName = extractTableName(trimmedSql);
                    if (tableName != null && !isTableExists(tableName)) {
                        log.info("Table {} does not exist, creating...", tableName);
                        try {
                            jdbcTemplate.execute(trimmedSql);
                            log.info("Successfully created table: {}", tableName);
                        } catch (Exception e) {
                            log.error("Failed to create table: {}", tableName, e);
                        }
                    } else {
                        log.info("Table {} already exists, skipping creation", tableName);
                    }
                }
            }

            // Apply lightweight schema patches for old databases that already have the table.
            ensureColumnExists(
                    "t_blog_settings",
                    "frontend_article_page_size",
                    "ALTER TABLE `t_blog_settings` ADD COLUMN `frontend_article_page_size` int(11) DEFAULT '12' COMMENT '前台文章列表每页数量' AFTER `logo_url`"
            );
            ensureColumnExists(
                    "t_article",
                    "article_source",
                    "ALTER TABLE `t_article` ADD COLUMN `article_source` tinyint(1) NOT NULL DEFAULT '1' COMMENT '文章来源：1-后台发布，2-前台发布' AFTER `author`"
            );
            ensureColumnExists(
                    "t_category",
                    "show_on_front",
                    "ALTER TABLE `t_category` ADD COLUMN `show_on_front` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否在前台导航展示：1-是，0-否' AFTER `illustrate`"
            );
            removeColumnIfExists("t_blog_settings", "slogan");
            removeColumnIfExists("t_blog_settings", "contact_email");
            
            // Then, process INSERT statements from both files (if enabled)
            if (executeInserts) {
                List<String> insertStatements = new ArrayList<>();
                
                // Add INSERT statements from CreateTable.sql (in case there are any)
                List<String> createTableSqlStatements = parseSqlFile("sql/CreateTable.sql");
                for (String sql : createTableSqlStatements) {
                    String trimmedSql = sql.trim();
                    if (trimmedSql.toUpperCase().startsWith("INSERT INTO")) {
                        insertStatements.add(trimmedSql);
                    }
                }
                
                // Add INSERT statements from InsertData.sql
                List<String> insertDataStatements = parseSqlFile("sql/InsertData.sql");
                for (String sql : insertDataStatements) {
                    String trimmedSql = sql.trim();
                    if (trimmedSql.toUpperCase().startsWith("INSERT INTO")) {
                        insertStatements.add(trimmedSql);
                    }
                }
                
                // Execute all INSERT statements
                for (String insertSql : insertStatements) {
                    try {
                        jdbcTemplate.execute(insertSql);
                        log.info("Successfully executed insert statement");
                    } catch (Exception e) {
                        // Log but don't fail - these might be duplicate inserts
                        log.warn("Failed to execute insert statement (may be duplicate): {}", e.getMessage());
                    }
                }
            } else {
                log.info("INSERT statement execution is disabled");
            }
            
            log.info("Database initialization check completed.");
        } catch (Exception e) {
            log.error("Error during database initialization", e);
        }
    }

    /**
     * Parse the SQL file and extract SQL statements
     */
    private List<String> parseSqlFile(String filePath) throws Exception {
        List<String> sqlStatements = new ArrayList<>();
        ClassPathResource resource = new ClassPathResource(filePath);
        
        try (InputStream inputStream = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            
            StringBuilder currentStatement = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                // Skip comments
                if (line.trim().startsWith("--")) {
                    continue;
                }
                
                currentStatement.append(line).append("\n");
                
                // If line ends with semicolon, it's a complete statement
                if (line.trim().endsWith(";")) {
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
     * Extract table name from CREATE TABLE statement
     */
    private String extractTableName(String createTableSql) {
        // Pattern to match CREATE TABLE `table_name` or CREATE TABLE table_name
        Pattern pattern = Pattern.compile("(?i)create\\s+table\\s+(?:`?)(\\w+)`?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(createTableSql);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }

    /**
     * Check if a table exists in the database
     */
    private boolean isTableExists(String tableName) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            // Handle different database types
            String schema = null;
            String catalog = null;
            
            // For MySQL, we typically need to specify the catalog
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
     * Add a column for legacy databases when it does not yet exist.
     */
    private void ensureColumnExists(String tableName, String columnName, String alterSql) {
        if (!isTableExists(tableName)) {
            return;
        }

        if (isColumnExists(tableName, columnName)) {
            log.info("Column {}.{} already exists, skipping patch", tableName, columnName);
            return;
        }

        try {
            log.info("Column {}.{} does not exist, applying patch...", tableName, columnName);
            jdbcTemplate.execute(alterSql);
            log.info("Successfully added column {}.{}", tableName, columnName);
        } catch (Exception e) {
            log.error("Failed to add column {}.{}", tableName, columnName, e);
        }
    }

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

    private void removeColumnIfExists(String tableName, String columnName) {
        if (!isTableExists(tableName) || !isColumnExists(tableName, columnName)) {
            return;
        }

        try {
            log.info("Removing unused column {}.{} ...", tableName, columnName);
            jdbcTemplate.execute(String.format("ALTER TABLE `%s` DROP COLUMN `%s`", tableName, columnName));
            log.info("Successfully removed unused column {}.{}", tableName, columnName);
        } catch (Exception e) {
            log.error("Failed to remove unused column {}.{}", tableName, columnName, e);
        }
    }
}
