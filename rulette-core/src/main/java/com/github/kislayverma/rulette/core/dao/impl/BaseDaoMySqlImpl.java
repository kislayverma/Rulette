package com.github.kislayverma.rulette.core.dao.impl;

import com.github.kislayverma.rulette.core.dao.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class BaseDaoMySqlImpl {

    protected Connection getConnection() throws SQLException, IOException {
        return DataSource.getInstance(null).getConnection();
    }
}
