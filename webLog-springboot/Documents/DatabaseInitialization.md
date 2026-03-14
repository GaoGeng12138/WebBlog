# Database Initialization Feature

This feature automatically checks for the existence of database tables on application startup and creates them if they don't exist.

## How It Works

1. When the Spring Boot application starts, it reads the SQL file located at `src/main/resources/sql/CreateTable.sql`
2. It parses the file to extract CREATE TABLE statements
3. For each table, it checks if the table already exists in the database
4. If a table doesn't exist, it creates the table using the corresponding CREATE TABLE statement
5. After all tables are created, it executes any INSERT statements in the file

## Configuration

The feature can be enabled/disabled using the following properties in your `application.yml` or `application.properties`:

```yaml
app:
  database:
    init:
      enabled: true        # Set to false to disable database initialization completely
      execute-inserts: true # Set to false to only create tables but not execute INSERT statements
```

By default, both features are enabled.

## Supported Databases

This feature has been tested with MySQL. It should work with other databases as well, but may require minor adjustments.

## File Format Requirements

The SQL file should follow these conventions:
- Each CREATE TABLE statement should end with a semicolon (;)
- Comments should start with -- 
- INSERT statements are supported and will be executed after all tables are created
- Table names in CREATE TABLE statements should follow standard SQL naming conventions

## Logging

The feature logs its activities at INFO level:
- When initialization starts and completes
- When checking for each table
- When creating tables
- When executing INSERT statements

## Error Handling

- If a table already exists, the creation is skipped silently
- If an INSERT statement fails (e.g., due to duplicate data), it's logged as a warning but doesn't stop the process
- Any critical errors during table creation are logged as errors

