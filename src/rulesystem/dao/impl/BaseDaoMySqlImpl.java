package rulesystem.dao.impl;

import java.io.IOException;
import java.sql.SQLException;
import rulesystem.dao.DataSource;

public class BaseDaoMySqlImpl {

    protected static DataSource dataSource;
    private static boolean INITIALIZED = false;

    public BaseDaoMySqlImpl() throws Exception {
        if (!INITIALIZED) {
            // This will load the MySQL driver
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new Exception(e);
            }

            initDatabaseConnection();
            INITIALIZED = true;
        }
    }

    // Source of the copy-paste : http://www.vogella.com/articles/MySQLJava/article.html
    private void initDatabaseConnection() throws SQLException, IOException {
        dataSource = DataSource.getInstance();
    }
}
