package com.github.kislayverma.rulette.core.dao.impl;

import com.github.kislayverma.rulette.core.dao.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class BaseDaoMySqlImpl {

//    private DataSource dataSource;
    private static boolean INITIALIZED = false;

    // Source of the copy-paste : http://www.vogella.com/articles/MySQLJava/article.html
    public BaseDaoMySqlImpl() throws Exception {
        if (!INITIALIZED) {
            // This will load the MySQL driver
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new Exception(e);
            }

//            initDatabaseConnection();
            INITIALIZED = true;
        }
    }

    protected Connection getConnection() throws SQLException, IOException {
        return DataSource.getInstance(null).getConnection();
    }

//    private void initDatabaseConnection() throws SQLException, IOException {
//        dataSource = DataSource.init();
//    }
}
