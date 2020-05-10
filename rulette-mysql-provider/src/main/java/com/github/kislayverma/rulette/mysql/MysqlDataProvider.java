/*
 * Copyright 2016 kislay.verma.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.kislayverma.rulette.mysql;

import com.github.kislayverma.rulette.core.data.IDataProvider;
import com.github.kislayverma.rulette.core.exception.DataAccessException;
import com.github.kislayverma.rulette.core.metadata.RuleInputMetaData;
import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaData;
import com.github.kislayverma.rulette.core.rule.Rule;
import com.github.kislayverma.rulette.core.ruleinput.type.RuleInputType;
import com.github.kislayverma.rulette.core.ruleinput.value.DefaultDataType;
import com.github.kislayverma.rulette.mysql.dao.DataSource;
import com.github.kislayverma.rulette.mysql.dao.RuleDao;
import com.github.kislayverma.rulette.mysql.dao.RuleInputDao;
import com.github.kislayverma.rulette.mysql.dao.RuleSystemDao;
import com.github.kislayverma.rulette.mysql.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * A MySql based implementation of the Rulette {@link IDataProvider} interface.
 * @author kislay.verma
 */
public class MysqlDataProvider implements IDataProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlDataProvider.class);

    private final RuleSystemDao ruleSystemDao;
    private final RuleInputDao ruleInputDao;
    private final RuleDao ruleDao;

    public MysqlDataProvider(String datasourceUrl) throws IOException, SQLException {
        this(Utils.readProperties(datasourceUrl));
    }

    public MysqlDataProvider(Properties props) throws IOException, SQLException {
        DataSource.init(props);
        this.ruleDao = new RuleDao();
        this.ruleSystemDao = new RuleSystemDao();
        this.ruleInputDao = new RuleInputDao();
        ruleDao.setRuleSystemDao(ruleSystemDao);
        ruleSystemDao.setRuleInputDao(ruleInputDao);
        ruleInputDao.setRuleSystemDao(ruleSystemDao);
    }

    private Connection getConnection() throws SQLException, IOException {
        return DataSource.getInstance(null).getConnection();
    }

    @Override
    public void createRuleSystem(RuleSystemMetaData ruleSystemMetaData) {
        String ruleSystemName = ruleSystemMetaData.getRuleSystemName();
        if (ruleSystemName == null || ruleSystemName.trim().isEmpty()) {
            throw new IllegalArgumentException("Rule system name not given for creation");
        }
        if (ruleSystemDao.getRuleSystemMetaData(ruleSystemName, null) != null) {
            throw new IllegalArgumentException("Rule system with given name already exists");
        }
        if (ruleSystemMetaData.getTableName() == null || ruleSystemMetaData.getTableName().trim().isEmpty() ||
            ruleSystemMetaData.getUniqueIdColumnName() == null || ruleSystemMetaData.getUniqueIdColumnName().trim().isEmpty() ||
            ruleSystemMetaData.getUniqueOutputColumnName() == null || ruleSystemMetaData.getUniqueOutputColumnName().trim().isEmpty()) {
            throw new IllegalArgumentException("All attributes of rule system must be set to create it");
        }

        // To do all these in a transaction, create a connection here, and pass it down the line
        Connection conn = null;
        boolean executionSuccess = false;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            ruleSystemDao.createRuleSystem(ruleSystemMetaData, conn);
            if (ruleSystemMetaData.getInputColumnList() != null) {
                for (RuleInputMetaData rimd : ruleSystemMetaData.getInputColumnList()) {
                    ruleInputDao.addRuleInput(ruleSystemName, rimd, conn);
                }
            }
            executionSuccess = true;

            // Reload rule system metadata
            ruleSystemDao.reloadRuleSystemMetaData(ruleSystemName, conn);
        } catch (IOException | SQLException ex) {
            throw new DataAccessException("Error getting database connection", ex);
        } finally {
            handleTransactionCompletion(conn, executionSuccess);
        }

    }

    @Override
    public void deleteRuleSystem(String ruleSystemName) {
        if (ruleSystemName == null || ruleSystemName.trim().isEmpty()) {
            throw new IllegalArgumentException("Rule system name not given for deletion");
        }
        RuleSystemMetaData existingRuleSystem = ruleSystemDao.getRuleSystemMetaData(ruleSystemName, null);
        if (existingRuleSystem == null) {
            throw new IllegalArgumentException("Rule system with given name does not exist");
        }

        // To do all these in a transaction, create a connection here, and pass it down the line
        Connection conn = null;
        boolean executionSuccess = false;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);
            // First delete the inputs, then the rule system
            if (existingRuleSystem.getInputColumnList() != null) {
                for (RuleInputMetaData rimd : existingRuleSystem.getInputColumnList()) {
                    ruleInputDao.deleteRuleInput(ruleSystemName, rimd.getName(), conn);
                }
            }
            ruleSystemDao.deleteRuleSystem(ruleSystemName, conn);
            executionSuccess = true;
        } catch (IOException | SQLException ex) {
            throw new DataAccessException("Error getting database connection", ex);
        } finally {
            handleTransactionCompletion(conn, executionSuccess);
        }
    }

    private RuleInputMetaData buildIdRuleInput(String colName) {
        return new RuleInputMetaData(
            colName, -1, RuleInputType.VALUE, DefaultDataType.STRING.name(), null ,null);
    }

    private void handleTransactionCompletion(Connection conn, boolean executionSuccess) {
        try {
            if (executionSuccess) {
                conn.commit();
            } else if (!conn.isClosed()) {
                conn.rollback();
            }
        } catch (SQLException e) {
            LOGGER.error("Error committing/rolling back transaction", e);
        } finally {
            Utils.closeSqlArtifacts(null, null, conn);
        }
    }

    @Override
    public List<Rule> getAllRules(String ruleSystemName) {
        return ruleDao.getAllRules(ruleSystemName, null);
    }

    @Override
    public Rule saveRule(String ruleSystemName, Rule rule) {
        return ruleDao.saveRule(ruleSystemName, rule, null);
    }

    @Override
    public boolean deleteRule(String ruleSystemName, Rule rule) {
        return ruleDao.deleteRule(ruleSystemName, rule, null);
    }

    @Override
    public Rule updateRule(String ruleSystemName, Rule rule) {
        return ruleDao.updateRule(ruleSystemName, rule, null);
    }

    @Override
    public void addRuleInput(String ruleSystemName, RuleInputMetaData ruleInput) {
        ruleInputDao.addRuleInput(ruleSystemName, ruleInput, null);
        // Reload rule system metadata
        ruleSystemDao.reloadRuleSystemMetaData(ruleSystemName, null);
    }

    @Override
    public void deleteRuleInput(String ruleSystemName, String ruleInputName) {
        ruleInputDao.deleteRuleInput(ruleSystemName, ruleInputName, null);
        // Reload rule system metadata
        ruleSystemDao.reloadRuleSystemMetaData(ruleSystemName, null);
    }

    @Override
    public RuleSystemMetaData getRuleSystemMetaData(String ruleSystemName) {
        return ruleSystemDao.getRuleSystemMetaData(ruleSystemName, null);
    }
}
