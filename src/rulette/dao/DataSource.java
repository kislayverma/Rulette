package rulette.dao;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 *
 * @author kislay
 */
public class DataSource {

    /*
     * A singleton that represents a pooled datasource. It is composed of a C3PO
     * pooled datasource. Can be changed to any connect pool provider
     */
    private Properties props;
    private ComboPooledDataSource cpds;
    private static DataSource datasource;

    private DataSource() throws IOException, SQLException {
        // load datasource properties
        props = Utils.readProperties("datasource.properties");
        cpds = new ComboPooledDataSource();
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
            testConnection = cpds.getConnection();
            testStatement = testConnection.createStatement();
            testStatement.executeQuery("select 1+1 from DUAL");
        } catch (SQLException e) {
            throw e;
        } finally {
            if (testStatement != null)
                testStatement.close();
            if (testConnection != null)
                testConnection.close();
        }
    }

    public static DataSource getInstance() throws IOException, SQLException {
        if (datasource == null) {
            datasource = new DataSource();
        }

        return datasource;
    }

    public Connection getConnection() throws SQLException {
        return this.cpds.getConnection();
    }
}
