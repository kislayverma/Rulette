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
 *
 * @author kislay
 */
public class DataSource {

    private static DataSource datasource;
    private HikariDataSource hikariDatasource;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSource.class);

    public static void init(String fileName) throws IOException, SQLException {
        if (datasource == null) {
            loadDriverClass();
            LOGGER.debug("File name is " + fileName);
            datasource = new DataSource(fileName);
        }
    }

    public static void init(Properties props) throws IOException, SQLException {
        if (datasource == null) {
            loadDriverClass();
            LOGGER.debug("Input properties " + props.toString());
            datasource = new DataSource(props);
        }
    }

    public static DataSource getInstance(String fileName) throws IOException, SQLException {
        if (datasource == null) {
            init(fileName);
        }

        return datasource;
    }

    public Connection getConnection() throws SQLException {
        return this.hikariDatasource.getConnection();
    }

    private DataSource(String fileName) throws IOException, SQLException {
        this(Utils.getHikariConfig(fileName));
    }

    private DataSource(Properties props) throws IOException, SQLException {
        this(Utils.getHikariConfig(props));
    }

    private DataSource(HikariConfig hikariConfig) throws SQLException {
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
