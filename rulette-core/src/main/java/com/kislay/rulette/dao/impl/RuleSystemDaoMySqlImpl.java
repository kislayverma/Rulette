package com.kislay.rulette.dao.impl;

import com.kislay.rulette.dao.RuleSystemDao;
import com.kislay.rulette.metadata.RuleSystemMetaData;
import com.kislay.rulette.metadata.RuleSystemMetaDataFactory;
import com.kislay.rulette.rule.Rule;
import com.kislay.rulette.ruleinput.RuleInputMetaData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleSystemDaoMySqlImpl extends BaseDaoMySqlImpl implements RuleSystemDao {

    public RuleSystemDaoMySqlImpl() throws Exception {
        super();
    }

    @Override
    public List<Rule> getAllRules(String ruleSystemName) throws SQLException, Exception {
        List<Rule> rules = new ArrayList<>();

        Statement statement = dataSource.getConnection().createStatement();
        ResultSet resultSet =
            statement.executeQuery("SELECT * " + " FROM " + RuleSystemMetaDataFactory.getInstance().getMetaData(ruleSystemName).getTableName());

        if (resultSet.first()) {
            rules = convertToRules(resultSet, RuleSystemMetaDataFactory.getInstance().getMetaData(ruleSystemName));
        }

        return rules;
    }

    @Override
    public Rule saveRule(String ruleSystemName, Rule rule) throws SQLException, Exception {
        RuleSystemMetaData metaData = RuleSystemMetaDataFactory.getInstance().getMetaData(ruleSystemName);

        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder nameListBuilder = new StringBuilder();
        StringBuilder valueListBuilder = new StringBuilder();

        for (RuleInputMetaData col : metaData.getInputColumnList()) {
            nameListBuilder.append(col.getName()).append(",");
            String val = rule.getColumnData(col.getName()).getRawValue();
            valueListBuilder.append(val.isEmpty() ? null : "'" + val + "'").append(",");
        }
        nameListBuilder.append(metaData.getUniqueOutputColumnName()).append(",");
        valueListBuilder.append(rule.getColumnData(metaData.getUniqueOutputColumnName()).getRawValue()).append(",");

        sqlBuilder.append("INSERT INTO ")
                .append(metaData.getTableName())
                .append(" (").append(nameListBuilder.toString().substring(0, nameListBuilder.length() - 1)).append(") ")
                .append(" VALUES (").append(valueListBuilder.toString().substring(0, valueListBuilder.length() - 1)).append(") ");

        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement =
                connection.prepareStatement("SELECT * " + " FROM " + metaData.getTableName());

        if (preparedStatement.executeUpdate(
                sqlBuilder.toString(), Statement.RETURN_GENERATED_KEYS) > 0) {
            // Get the rule object for returning using LAST_INSERT_ID() MySql function.
            // This id is maintained per connection so multiple instances inserting rows
            // isn't a problem.
            preparedStatement =
                    connection.prepareStatement("SELECT * FROM " + metaData.getTableName()
                    + " WHERE " + metaData.getUniqueIdColumnName()
                    + " = LAST_INSERT_ID()");
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Rule> ruleList = convertToRules(resultSet, metaData);
            if (ruleList != null && !ruleList.isEmpty()) {
                return ruleList.get(0);
            }
        }

        return null;
    }

    @Override
    public boolean deleteRule(String ruleSystemName, Rule rule) throws SQLException, Exception {
        RuleSystemMetaData metaData = RuleSystemMetaDataFactory.getInstance().getMetaData(ruleSystemName);

        String sql = "DELETE FROM " + metaData.getTableName()
                + " WHERE " + metaData.getUniqueIdColumnName() + "= ?";
        PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(sql);
        preparedStatement.setString(1, rule.getColumnData(metaData.getUniqueIdColumnName()).getRawValue());

        return preparedStatement.executeUpdate() > 0;
    }

    @Override
    public Rule updateRule(String ruleSystemName, Rule rule) throws SQLException, Exception {
        RuleSystemMetaData metaData = RuleSystemMetaDataFactory.getInstance().getMetaData(ruleSystemName);

        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder updateListBuilder = new StringBuilder();

        for (RuleInputMetaData col : metaData.getInputColumnList()) {
            String val = rule.getColumnData(col.getName()).getRawValue();

            updateListBuilder.append(col.getName())
                    .append("=")
                    .append("".equals(val) ? null : "'" + val + "'")
                    .append(",");
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

        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement =
                connection.prepareStatement(sqlBuilder.toString());
        if (preparedStatement.executeUpdate() > 0) {
            preparedStatement =
                    connection.prepareStatement("SELECT * FROM " + metaData.getTableName()
                    + " WHERE " + metaData.getUniqueIdColumnName()
                    + "=" + oldRuleId);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Rule> ruleList = convertToRules(resultSet, metaData);
            if (ruleList != null && !ruleList.isEmpty()) {
                return ruleList.get(0);
            }
        }

        return null;
    }

    private List<Rule> convertToRules(ResultSet resultSet, RuleSystemMetaData metadata) throws Exception {
        List<Rule> rules = new ArrayList<>();

        if (resultSet != null) {
            while (resultSet.next()) {
                Map<String, String> inputMap = new HashMap<>();

                for (RuleInputMetaData col : metadata.getInputColumnList()) {
                    inputMap.put(col.getName(), resultSet.getString(col.getName()));
                }
                inputMap.put(metadata.getUniqueIdColumnName(),
                    resultSet.getString(metadata.getUniqueIdColumnName()));
                inputMap.put(metadata.getUniqueOutputColumnName(),
                    resultSet.getString(metadata.getUniqueOutputColumnName()));

                rules.add(new Rule(metadata.getRuleSystemName(), inputMap));
            }
        }

        return rules;
    }
}
