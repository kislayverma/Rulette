package com.github.kislayverma.rulette.mysql.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseDao {
    private final DataSource dataSource;

    protected BaseDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected Connection getConnection() throws SQLException, IOException {
        return this.dataSource.getConnection();
    }
}
