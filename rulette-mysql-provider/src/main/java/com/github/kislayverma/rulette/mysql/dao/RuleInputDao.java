package com.github.kislayverma.rulette.mysql.dao;

import com.github.kislayverma.rulette.core.exception.DataAccessException;
import com.github.kislayverma.rulette.core.metadata.RuleInputMetaData;
import com.github.kislayverma.rulette.core.ruleinput.type.RuleInputType;
import com.github.kislayverma.rulette.mysql.model.RuleSystemMetadataMysqlModel;
import com.github.kislayverma.rulette.mysql.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RuleInputDao extends BaseDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleInputDao.class);

    private RuleSystemDao ruleSystemDao;

    public RuleInputDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<RuleInputMetaData> getRuleInputs(String ruleSystemName, Connection conn) throws SQLException, IOException {
        List<RuleInputMetaData> inputs = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = conn == null ? getConnection() : conn;
            statement = connection.createStatement();
            resultSet =
                statement.executeQuery("SELECT b.* "
                    + "FROM rule_system AS a "
                    + "JOIN rule_input AS b "
                    + "    ON b.rule_system_id = a.id "
                    + "WHERE a.name LIKE '" + ruleSystemName + "' "
                    + "ORDER BY b.priority ASC ");

            while (resultSet.next()) {
                RuleInputType ruleType =
                    "Value".equalsIgnoreCase(resultSet.getString("rule_type"))
                        ? RuleInputType.VALUE : RuleInputType.RANGE;
                String dataType = resultSet.getString("data_type").toUpperCase();

                inputs.add(new RuleInputMetaData(
                    resultSet.getString("name"),
                    resultSet.getInt("priority"),
                    ruleType,
                    dataType,
                    resultSet.getString("range_lower_bound_field_name"),
                    resultSet.getString("range_upper_bound_field_name")));
            }
        } finally {
            Utils.closeSqlArtifacts(resultSet, statement, (conn == null) ? connection : null);
        }

        return inputs;
    }

    public void addRuleInput(String ruleSystemName, RuleInputMetaData ruleInput, Connection conn) {
        RuleSystemMetadataMysqlModel metaData = ruleSystemDao.getRuleSystemMetaData(ruleSystemName, conn);

        StringBuilder sqlBuilder = new StringBuilder();

        StringBuilder nameListBuilder = new StringBuilder()
            .append("`name`").append(",")
            .append("`rule_system_id`").append(",")
            .append("`priority`").append(",")
            .append("`rule_type`").append(",")
            .append("`data_type`").append(",")
            .append("`range_lower_bound_field_name`").append(",")
            .append("`range_upper_bound_field_name`");
        StringBuilder valueListBuilder = new StringBuilder()
            .append("'").append(ruleInput.getName()).append("',")
            .append("'").append(metaData.getRuleSystemId()).append("',")
            .append(ruleInput.getPriority()).append(",")
            .append("'").append(ruleInput.getRuleInputType().name()).append("',")
            .append("'").append(ruleInput.getDataType()).append("',");
        if (ruleInput.getRangeLowerBoundFieldName() == null || ruleInput.getRangeLowerBoundFieldName().trim().isEmpty()) {
            valueListBuilder.append("NULL,");
        } else {
            valueListBuilder.append("'").append(ruleInput.getRangeLowerBoundFieldName()).append("',");
        }
        if (ruleInput.getRangeUpperBoundFieldName() == null || ruleInput.getRangeUpperBoundFieldName().trim().isEmpty()) {
            valueListBuilder.append("NULL");
        } else {
            valueListBuilder.append("'").append(ruleInput.getRangeUpperBoundFieldName()).append("'");
        }

        sqlBuilder.append("INSERT INTO rule_input ")
            .append(" (").append(nameListBuilder.toString()).append(") ")
            .append(" VALUES (").append(valueListBuilder.toString()).append(") ");
        LOGGER.info("Rule input insert query : {}", sqlBuilder.toString());

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = conn == null ? getConnection() : conn;
            preparedStatement = connection.prepareStatement(sqlBuilder.toString());
            // Add rule input
            preparedStatement.executeUpdate(sqlBuilder.toString());
        } catch (IOException | SQLException e) {
            throw new DataAccessException("Error saving new rule input", e);
        } finally {
            Utils.closeSqlArtifacts(null, preparedStatement, (conn == null) ? connection : null);
        }
    }

    public void deleteRuleInput(String ruleSystemName, String ruleInputName, Connection conn) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            RuleSystemMetadataMysqlModel metaData = ruleSystemDao.getRuleSystemMetaData(ruleSystemName, conn);

            String sql = "DELETE FROM rule_input "
                + " WHERE rule_system_id = " + metaData.getRuleSystemId() + " AND name = ?";

            connection = conn == null ? getConnection() : conn;
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, ruleInputName);

            preparedStatement.executeUpdate();
        } catch (IOException | SQLException e) {
            throw new DataAccessException("Error in deleting rule input", e);
        } finally {
            Utils.closeSqlArtifacts(null, preparedStatement, (conn == null) ? connection : null);
        }
    }

    public void setRuleSystemDao(RuleSystemDao ruleSystemDao) {
        this.ruleSystemDao = ruleSystemDao;
    }
}
