package com.workflex.workation.config;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class MultiTenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl implements HibernatePropertiesCustomizer {

    private static final String DEFAULT_TENANT_ID = "public";
    private final Map<String, Boolean> schemaCache = new ConcurrentHashMap<>();

    @Autowired
    private DataSource dataSource;

    @Override
    protected DataSource selectAnyDataSource() {
        return dataSource;
    }

    @Override
    protected DataSource selectDataSource(Object tenantIdentifier) {
        return dataSource;
    }

    @Override
    public Connection getConnection(Object tenantIdentifier) throws SQLException {
        Connection connection = super.getConnection(tenantIdentifier);
        
        try {
            String schemaName = tenantIdentifier != null ? tenantIdentifier.toString() : DEFAULT_TENANT_ID;
            
            // Create schema if it doesn't exist
            createSchemaIfNotExists(connection, schemaName);
            
            // Set the schema for this connection
            setSchema(connection, schemaName);
            
            log.debug("Connected to schema: {}", schemaName);
            
        } catch (SQLException e) {
            log.error("Error setting schema for tenant: {}", tenantIdentifier, e);
            // Fall back to default schema
            try {
                setSchema(connection, DEFAULT_TENANT_ID);
            } catch (SQLException fallbackException) {
                log.error("Error setting fallback schema", fallbackException);
                throw fallbackException;
            }
        }
        
        return connection;
    }

    @Override
    public void releaseConnection(Object tenantIdentifier, Connection connection) throws SQLException {
        try {
            // Reset to default schema before releasing
            setSchema(connection, DEFAULT_TENANT_ID);
        } catch (SQLException e) {
            log.warn("Error resetting schema before releasing connection", e);
        }
        super.releaseConnection(tenantIdentifier, connection);
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
    }

    private void createSchemaIfNotExists(Connection connection, String schemaName) throws SQLException {
        // Check cache first
        if (schemaCache.containsKey(schemaName)) {
            return;
        }

        try (Statement statement = connection.createStatement()) {
            // Create schema if it doesn't exist
            String createSchemaSQL = String.format("CREATE SCHEMA IF NOT EXISTS %s", sanitizeSchemaName(schemaName));
            statement.execute(createSchemaSQL);
            
            // Cache the schema creation
            schemaCache.put(schemaName, true);
            
            log.info("Schema created or verified: {}", schemaName);
        } catch (SQLException e) {
            log.error("Error creating schema: {}", schemaName, e);
            throw e;
        }
    }

    private void setSchema(Connection connection, String schemaName) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String setSchemaSQL = String.format("SET search_path TO %s", sanitizeSchemaName(schemaName));
            statement.execute(setSchemaSQL);
        }
    }

    private String sanitizeSchemaName(String schemaName) {
        if (schemaName == null || schemaName.trim().isEmpty()) {
            return DEFAULT_TENANT_ID;
        }
        
        // Remove any characters that are not alphanumeric or underscore
        String sanitized = schemaName.trim().replaceAll("[^a-zA-Z0-9_]", "");
        
        // Ensure it starts with a letter
        if (sanitized.isEmpty() || !Character.isLetter(sanitized.charAt(0))) {
            return DEFAULT_TENANT_ID;
        }
        
        // Limit length to 63 characters (PostgreSQL identifier limit)
        if (sanitized.length() > 63) {
            sanitized = sanitized.substring(0, 63);
        }
        
        return sanitized.toLowerCase();
    }

    /**
     * Clear the schema cache (useful for testing or when schemas are dropped)
     */
    public void clearSchemaCache() {
        schemaCache.clear();
        log.info("Schema cache cleared");
    }

    /**
     * Remove a specific schema from cache
     */
    public void removeSchemaFromCache(String schemaName) {
        schemaCache.remove(schemaName);
        log.info("Schema removed from cache: {}", schemaName);
    }

    /**
     * Check if a schema exists in cache
     */
    public boolean isSchemaInCache(String schemaName) {
        return schemaCache.containsKey(schemaName);
    }
}