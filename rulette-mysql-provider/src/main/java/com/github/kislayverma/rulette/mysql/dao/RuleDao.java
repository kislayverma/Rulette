package com.github.kislayverma.rulette.mysql.dao;

import com.github.kislayverma.rulette.core.exception.DataAccessException;
import com.github.kislayverma.rulette.core.metadata.RuleInputMetaData;
import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaData;
import com.github.kislayverma.rulette.core.rule.Rule;
import com.github.kislayverma.rulette.core.ruleinput.type.RuleInputType;
import com.github.kislayverma.rulette.core.ruleinput.value.DefaultDataType;
import com.github.kislayverma.rulette.mysql.util.Utils;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleDao extends BaseDao {
    private RuleSystemDao ruleSystemDao;

    public RuleDao(DataSource dataSource) {
        super(dataSource);
    }

    public List<Rule> getAllRules(String ruleSystemName, Connection conn) {
        List<Rule> rules = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = conn == null ? getConnection() : conn;
            statement = connection.createStatement();
            resultSet =
                statement.executeQuery("SELECT * " + " FROM "
                    + ruleSystemDao.getRuleSystemMetaData(ruleSystemName, connection).getTableName());

            if (resultSet != null) {
                rules = convertToRules(resultSet, ruleSystemDao.getRuleSystemMetaData(ruleSystemName, connection));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Utils.closeSqlArtifacts(resultSet, statement, (conn == null) ? connection : null);
        }

        return rules;
    }

    public Rule saveRule(String ruleSystemName, Rule rule, Connection conn) {
        RuleSystemMetaData metaData = ruleSystemDao.getRuleSystemMetaData(ruleSystemName, conn);

        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder nameListBuilder = new StringBuilder();
        StringBuilder valueListBuilder = new StringBuilder();

        for (RuleInputMetaData col : metaData.getInputColumnList()) {
            if (RuleInputType.VALUE == col.getRuleInputType()) {
                nameListBuilder.append(col.getName()).append(",");
                String val = rule.getColumnData(col.getName()).getRawValue();
                valueListBuilder.append(val.isEmpty() ? null : "'" + val + "'").append(",");
            } else {
                String[] values = rule.getColumnData(col.getName()).getRawValue().split("-");
                // If the input is essentially empty, don't add it to the query
                if (values.length > 1 && (!values[0].isEmpty() || !values[1].isEmpty())) {
                    nameListBuilder
                        .append(col.getRangeLowerBoundFieldName()).append(",")
                        .append(col.getRangeUpperBoundFieldName()).append(",");
                    valueListBuilder
                        .append(values[0].trim().isEmpty() ? null : "'" + values[0] + "'").append(",")
                        .append(values[1].trim().isEmpty() ? null : "'" + values[1] + "'").append(",");
                }
            }
        }
        nameListBuilder.append(metaData.getUniqueOutputColumnName()).append(",");
        valueListBuilder.append(rule.getColumnData(metaData.getUniqueOutputColumnName()).getRawValue()).append(",");

        sqlBuilder.append("INSERT INTO ")
            .append(metaData.getTableName())
            .append(" (").append(nameListBuilder.toString().substring(0, nameListBuilder.length() - 1)).append(") ")
            .append(" VALUES (").append(valueListBuilder.toString().substring(0, valueListBuilder.length() - 1)).append(") ");

        List<Rule> ruleList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = conn == null ? getConnection() : conn;
            preparedStatement =
                connection.prepareStatement("SELECT * " + " FROM " + metaData.getTableName());
            resultSet = null;

            if (preparedStatement.executeUpdate(sqlBuilder.toString(), Statement.RETURN_GENERATED_KEYS) > 0) {
                // Get the rule object for returning using LAST_INSERT_ID() MySql function.
                // This id is maintained per connection so multiple instances inserting rows
                // isn't a problem.
                preparedStatement =
                    connection.prepareStatement("SELECT * FROM " + metaData.getTableName()
                        + " WHERE " + metaData.getUniqueIdColumnName()
                        + " = LAST_INSERT_ID()");
                resultSet = preparedStatement.executeQuery();

                ruleList = convertToRules(resultSet, metaData);

            }

        } catch (SQLException | IOException e) {
            throw new DataAccessException("Error in saving new rule", e);
        } finally {
            Utils.closeSqlArtifacts(resultSet, preparedStatement, (conn == null) ? connection : null);
        }
        if (ruleList != null && !ruleList.isEmpty()) {
            return ruleList.get(0);
        }
        return null;
    }

    public boolean deleteRule(String ruleSystemName, Rule rule, Connection conn) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = conn == null ? getConnection() : conn;
            RuleSystemMetaData metaData = ruleSystemDao.getRuleSystemMetaData(ruleSystemName, connection);

            String sql = "DELETE FROM " + metaData.getTableName()
                + " WHERE " + metaData.getUniqueIdColumnName() + "= ?";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, rule.getColumnData(metaData.getUniqueIdColumnName()).getRawValue());

            return preparedStatement.executeUpdate() > 0;
        } catch (IOException | SQLException e) {
            throw new DataAccessException("Error in deleting updated rule", e);
        } finally {
            Utils.closeSqlArtifacts(null, preparedStatement, (conn == null) ? connection : null);
        }
    }

    public Rule updateRule(String ruleSystemName, Rule rule, Connection conn) {
        RuleSystemMetaData metaData = ruleSystemDao.getRuleSystemMetaData(ruleSystemName, conn);

        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder updateListBuilder = new StringBuilder();

        for (RuleInputMetaData col : metaData.getInputColumnList()) {
            if (RuleInputType.VALUE == col.getRuleInputType()) {
                String val = rule.getColumnData(col.getName()).getRawValue();
                updateListBuilder.append(col.getName())
                    .append("=")
                    .append(val.trim().isEmpty() ? null : "'" + val + "'")
                    .append(",");
            } else {
                String[] values = rule.getColumnData(col.getName()).getRawValue().split("-");
                // If the input is essentially empty, don't add it to the query
                if (values.length > 1 && (!values[0].isEmpty() || !values[1].isEmpty())) {
                    updateListBuilder
                        .append(col.getRangeLowerBoundFieldName())
                        .append("=")
                        .append(values[0].trim().isEmpty() ? null : "'" + values[0] + "'")
                        .append(",")
                        .append(col.getRangeUpperBoundFieldName())
                        .append("=")
                        .append(values[1].trim().isEmpty() ? null : "'" + values[1] + "'")
                        .append(",");
                }
            }
        }
        updateListBuilder.append(metaData.getUniqueOutputColumnName())
            .append("=")
            .append(rule.getColumnData(metaData.getUniqueOutputColumnName()).getRawValue())
            .append(",");

        String oldRuleId = rule.getColumnData(metaData.getUniqueIdColumnName()).getRawValue();
        sqlBuilder.append("UPDATE ")
            .append(metaData.getTableName())
            .append(" SET ")
            .append(updateListBuilder.toString().substring(0, updateListBuilder.length() - 1))
            .append(" WHERE ")
            .append(metaData.getUniqueIdColumnName())
            .append("=")
            .append(oldRuleId);

        List<Rule> ruleList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = conn == null ? getConnection() : conn;
            preparedStatement =
                connection.prepareStatement(sqlBuilder.toString());
            resultSet = null;
            if (preparedStatement.executeUpdate() > 0) {
                preparedStatement =
                    connection.prepareStatement("SELECT * FROM " + metaData.getTableName()
                        + " WHERE " + metaData.getUniqueIdColumnName()
                        + "=" + oldRuleId);
                resultSet = preparedStatement.executeQuery();

                ruleList = convertToRules(resultSet, metaData);
            }
        } catch (IOException | SQLException e) {
            throw new DataAccessException("Error in storing updated rule", e);
        } finally {
            Utils.closeSqlArtifacts(resultSet, preparedStatement, (conn == null) ? connection : null);
        }

        if (ruleList != null && !ruleList.isEmpty()) {
            return ruleList.get(0);
        }

        return null;
    }

    private List<Rule> convertToRules(ResultSet resultSet, RuleSystemMetaData metadata) throws SQLException {
        List<Rule> rules = new ArrayList<>();

        if (resultSet != null) {
            while (resultSet.next()) {
                Map<String, String> inputMap = new HashMap<>();

                for (RuleInputMetaData col : metadata.getInputColumnList()) {
                    if (col.getRuleInputType() == RuleInputType.RANGE) {
                        String lowerBoundFieldName = resultSet.getString(col.getRangeLowerBoundFieldName());
                        String upperBoundFieldName = resultSet.getString(col.getRangeUpperBoundFieldName());
                        if(col.getDataType().equals(DefaultDataType.DATE.name())){
                            inputMap.put(col.getRangeLowerBoundFieldName(), lowerBoundFieldName != null ? lowerBoundFieldName.substring(0, 19) : lowerBoundFieldName);
                            inputMap.put(col.getRangeUpperBoundFieldName(), upperBoundFieldName != null ? upperBoundFieldName.substring(0, 19) : upperBoundFieldName);
                        }else {
                            inputMap.put(col.getRangeLowerBoundFieldName(), lowerBoundFieldName);
                            inputMap.put(col.getRangeUpperBoundFieldName(), upperBoundFieldName);
                        }
                    } else {
                        inputMap.put(col.getName(), resultSet.getString(col.getName()));
                    }
                }
                inputMap.put(metadata.getUniqueIdColumnName(),
                    resultSet.getString(metadata.getUniqueIdColumnName()));
                inputMap.put(metadata.getUniqueOutputColumnName(),
                    resultSet.getString(metadata.getUniqueOutputColumnName()));


                rules.add(new Rule(metadata, inputMap));
            }
        }

        return rules;
    }

    public void setRuleSystemDao(RuleSystemDao ruleSystemDao) {
        this.ruleSystemDao = ruleSystemDao;
    }
}
