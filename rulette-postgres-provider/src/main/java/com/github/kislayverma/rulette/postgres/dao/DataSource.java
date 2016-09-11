package com.github.kislayverma.rulette.postgres.dao;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kislay
 */
public class DataSource {

    private Properties props;
    private ComboPooledDataSource cpds;
    private static DataSource datasource;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSource.class);

    private DataSource(String fileName) throws IOException, SQLException {
        // load datasource properties
        props = Utils.readProperties(fileName);
        cpds = new ComboPooledDataSource();
        try {
            cpds.setDriverClass(props.getProperty("driverClass"));
        } catch (PropertyVetoException ex) {
            throw new RuntimeException(ex);
        }
        cpds.setJdbcUrl(props.getProperty("jdbcUrl"));
        cpds.setUser(props.getProperty("username"));
        cpds.setPassword(props.getProperty("password"));
        cpds.setInitialPoolSize(new Integer((String) props.getProperty("initialPoolSize")));
        cpds.setAcquireIncrement(new Integer((String) props.getProperty("acquireIncrement")));
        cpds.setMaxPoolSize(new Integer((String) props.getProperty("maxPoolSize")));
        cpds.setMinPoolSize(new Integer((String) props.getProperty("minPoolSize")));
        cpds.setMaxStatements(new Integer((String) props.getProperty("maxStatements")));

        Connection testConnection = null;
        Statement testStatement = null;

        // test connectivity and initialize pool
        try {
            LOGGER.info("Testing DB connection...");
            testConnection = cpds.getConnection();
            testStatement = testConnection.createStatement();
            testStatement.executeQuery("select 1+1 from DUAL");

            LOGGER.info("DB connection tested successfully.");
        } catch (SQLException e) {
            throw e;
        } finally {
            if (testStatement != null)
                testStatement.close();
            if (testConnection != null)
                testConnection.close();
        }
    }

    public static void init(String fileName) throws IOException, SQLException {
        if (datasource == null) {
            loadDriverClass();
            LOGGER.debug("File name is " + fileName);
            datasource = new DataSource(fileName);
        }
    }

    // This will load the PostgreSQL driver
    // Source of the copy-paste : http://www.vogella.com/articles/MySQLJava/article.html
    private static void loadDriverClass() {
        try {
            Class.forName("org.postgresql.Driver").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static DataSource getInstance(String fileName) throws IOException, SQLException {
        if (datasource == null) {
            init(fileName);
        }

        return datasource;
    }

    public Connection getConnection() throws SQLException {
        return this.cpds.getConnection();
    }
}
