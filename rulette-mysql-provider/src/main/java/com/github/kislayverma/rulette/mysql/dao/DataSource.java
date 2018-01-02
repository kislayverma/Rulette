package com.github.kislayverma.rulette.mysql.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 *
 * @author kislay
 */
public class DataSource {

    private static DataSource datasource;
    private HikariDataSource hikariDatasource;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSource.class);

    private DataSource(String fileName) throws IOException, SQLException {
        // load datasource properties
        HikariConfig hikariConfig = getHikariConfig(fileName);
        hikariDatasource = new HikariDataSource(hikariConfig);

        Connection testConnection = null;
        Statement testStatement = null;

        // test connectivity and initialize pool
        try {
            LOGGER.info("Testing DB connection...");
            testConnection = hikariDatasource.getConnection();
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

    // This will load the MySQL driver
    // Source of the copy-paste : http://www.vogella.com/articles/MySQLJava/article.html
    private static void loadDriverClass() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
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
        return hikariDatasource.getConnection();
    }

    private HikariConfig getHikariConfig(String fileName) throws IOException {

        Properties props = Utils.readProperties(fileName);
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(props.getProperty("driverClass"));
        hikariConfig.setJdbcUrl(props.getProperty("jdbcUrl"));
        hikariConfig.setUsername(props.getProperty("username"));
        hikariConfig.setPassword(props.getProperty("password"));
        hikariConfig.setMaximumPoolSize(new Integer((String) props.getProperty("maxPoolSize")));
        hikariConfig.setConnectionTimeout(new Long((String) props.getProperty("connectionTimeout")));

        return hikariConfig;
    }
}
