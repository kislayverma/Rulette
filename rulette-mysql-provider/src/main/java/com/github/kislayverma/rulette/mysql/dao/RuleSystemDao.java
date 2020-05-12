package com.github.kislayverma.rulette.mysql.dao;

import com.github.kislayverma.rulette.core.exception.DataAccessException;
import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaData;
import com.github.kislayverma.rulette.mysql.model.RuleSystemMetadataMysqlModel;
import com.github.kislayverma.rulette.mysql.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuleSystemDao extends BaseDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleSystemDao.class);
    private final Map<String, RuleSystemMetadataMysqlModel> metaDataMap = new ConcurrentHashMap<>();

    private RuleInputDao ruleInputDao;

    public RuleSystemMetadataMysqlModel getRuleSystemMetaData(String ruleSystemName, Connection conn) {
        RuleSystemMetadataMysqlModel rsMetadataMysqlModel = metaDataMap.get(ruleSystemName);

        if (rsMetadataMysqlModel == null) {
            rsMetadataMysqlModel = loadRuleSystemMetaData(ruleSystemName, conn);
            if (rsMetadataMysqlModel != null) {
                metaDataMap.put(ruleSystemName, rsMetadataMysqlModel);
            }
        }

        return rsMetadataMysqlModel;
    }

    public List<RuleSystemMetadataMysqlModel> getAllRuleSystemMetaData(Connection conn) {
        final List<RuleSystemMetadataMysqlModel> allRuleSystems = new ArrayList<>();
        getAllRuleSystemNames(conn).stream().forEach(ruleSystem -> {
            allRuleSystems.add(ruleSystem);
        });

        return allRuleSystems;
    }

    private List<RuleSystemMetadataMysqlModel> getAllRuleSystemNames(Connection conn) {
        List<RuleSystemMetadataMysqlModel> ruleSystemNames = new ArrayList<>();

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = conn == null ? getConnection() : conn;
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM rule_system");
            if (resultSet != null) {
                while (resultSet.next()) {
                    String ruleSystemName = resultSet.getString("name");
                    ruleSystemNames.add(
                        new RuleSystemMetadataMysqlModel(
                            ruleSystemName,
                            resultSet.getString("table_name"),
                            resultSet.getString("unique_id_column_name"),
                            resultSet.getString("output_column_name"),
                            ruleInputDao.getRuleInputs(ruleSystemName, connection),
                            resultSet.getLong("id")));
                }
            }
        } catch (IOException | SQLException e) {
            throw new DataAccessException("Error loading all rule systems", e);
        } finally {
            Utils.closeSqlArtifacts(resultSet, statement, (conn == null) ? connection : null);
        }

        return ruleSystemNames;
    }

    public void createRuleSystem(RuleSystemMetaData ruleSystemMetaData, Connection conn) {
        String ruleSystemName = ruleSystemMetaData.getRuleSystemName();
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        StringBuilder nameListBuilder = new StringBuilder()
            .append("`name`").append(",")
            .append("`table_name`").append(",")
            .append("`output_column_name`").append(",")
            .append("`unique_id_column_name`");
        StringBuilder valueListBuilder = new StringBuilder()
            .append("'").append(ruleSystemName).append("',")
            .append("'").append(ruleSystemMetaData.getTableName()).append("',")
            .append("'").append(ruleSystemMetaData.getUniqueOutputColumnName()).append("',")
            .append("'").append(ruleSystemMetaData.getUniqueIdColumnName()).append("'");
        StringBuilder sqlBuilder = new StringBuilder()
            .append("INSERT INTO rule_system ")
            .append(" (").append(nameListBuilder.toString()).append(") ")
            .append(" VALUES (").append(valueListBuilder.toString()).append(") ");

        LOGGER.info("Rule system insert query : {}", sqlBuilder.toString());

        try {
            connection = conn == null ? getConnection() : conn;
            preparedStatement = connection.prepareStatement(sqlBuilder.toString());
            // Add rule system
            preparedStatement.executeUpdate(sqlBuilder.toString());
        } catch (IOException | SQLException e) {
            throw new DataAccessException("Error saving new rule system", e);
        } finally {
            Utils.closeSqlArtifacts(null, preparedStatement, (conn == null) ? connection : null);
        }
    }

    public void deleteRuleSystem(String ruleSystemName, Connection conn) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            String ruleSystemSql = "DELETE FROM rule_system WHERE name = ?";

            connection = conn == null ? getConnection() : conn;
            preparedStatement = connection.prepareStatement(ruleSystemSql);
            preparedStatement.setString(1, ruleSystemName);

            preparedStatement.executeUpdate();

            // Remove cached value
            this.metaDataMap.remove(ruleSystemName);
        } catch (IOException | SQLException e) {
            throw new DataAccessException("Error in deleting rule system", e);
        } finally {
            Utils.closeSqlArtifacts(null, preparedStatement, (conn == null) ? connection : null);
        }
    }

    public void reloadRuleSystemMetaData(String ruleSystemName, Connection conn) {
        metaDataMap.put(ruleSystemName, loadRuleSystemMetaData(ruleSystemName, conn));
    }

    private RuleSystemMetadataMysqlModel loadRuleSystemMetaData(String ruleSystemName, Connection conn) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = conn == null ? getConnection() : conn;
            statement = connection.createStatement();
            resultSet =
                statement.executeQuery("SELECT * FROM rule_system WHERE name LIKE '" + ruleSystemName + "'");

            if (!resultSet.first()) {
                return null;
            }

            return new RuleSystemMetadataMysqlModel(
                resultSet.getString("name"),
                resultSet.getString("table_name"),
                resultSet.getString("unique_id_column_name"),
                resultSet.getString("output_column_name"),
                ruleInputDao.getRuleInputs(ruleSystemName, connection),
                resultSet.getLong("id"));
        } catch (IOException | SQLException e) {
            throw new DataAccessException("Error loading rule system meta data", e);
        } finally {
            Utils.closeSqlArtifacts(resultSet, statement, (conn == null) ? connection : null);
        }
    }

    public void setRuleInputDao(RuleInputDao ruleInputDao) {
        this.ruleInputDao = ruleInputDao;
    }
}
