package com.github.kislayverma.rulette.mysql.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.github.kislayverma.rulette.mysql.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * This class represents the underlying MySQL connection pool
 */
public class DataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSource.class);

    private HikariDataSource hikariDatasource;

    public DataSource(String fileName) throws IOException, SQLException {
        this(Utils.getHikariConfig(fileName));
    }

    public DataSource(Properties props) throws IOException, SQLException {
        this(Utils.getHikariConfig(props));
    }

    public DataSource(HikariConfig hikariConfig) throws SQLException {
        loadDriverClass();

        hikariDatasource = new HikariDataSource(hikariConfig);

        Connection testConnection = null;
        Statement testStatement = null;

        // test connectivity and initialize pool
        try {
            LOGGER.debug("Testing DB connection...");
            testConnection = hikariDatasource.getConnection();
            testStatement = testConnection.createStatement();
            testStatement.executeQuery("select 1+1 from DUAL");

            LOGGER.debug("DB connection tested successfully.");
        } catch (SQLException e) {
            throw e;
        } finally {
            if (testStatement != null)
                testStatement.close();
            if (testConnection != null)
                testConnection.close();
        }
    }

    /**
     * Returns a MySQL connection from the connection pool
     */
    public Connection getConnection() throws SQLException {
        return this.hikariDatasource.getConnection();
    }

    // This will load the MySQL driver
    // Source of the copy-paste : http://www.vogella.com/articles/MySQLJava/article.html
    private static void loadDriverClass() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
