package com.github.kislayverma.rulette.mysql.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseDao {

    protected Connection getConnection() throws SQLException, IOException {
        return DataSource.getInstance(null).getConnection();
    }
}
