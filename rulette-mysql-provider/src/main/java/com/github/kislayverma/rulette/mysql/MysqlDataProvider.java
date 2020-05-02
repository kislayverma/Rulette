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
import com.github.kislayverma.rulette.mysql.model.RuleSystemMetadataMysqlModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A MySql based implementation of the Rulette {@link IDataProvider} interface.
 * @author kislay.verma
 */
public class MysqlDataProvider implements IDataProvider {
    private  static final Logger LOGGER = LoggerFactory.getLogger(MysqlDataProvider.class);

    private final Map<String, RuleSystemMetadataMysqlModel> metaDataMap;

    public MysqlDataProvider(String datasourceUrl) throws IOException, SQLException {
        metaDataMap = new ConcurrentHashMap<>();
        DataSource.init(datasourceUrl);
    }

    public MysqlDataProvider(Properties props) throws IOException, SQLException {
        metaDataMap = new ConcurrentHashMap<>();
        DataSource.init(props);
    }

    private Connection getConnection() throws SQLException, IOException {
        return DataSource.getInstance(null).getConnection();
    }

    @Override
    public List<Rule> getAllRules(String ruleSystemName) {
        List<Rule> rules = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            statement = connection.createStatement();
            resultSet =
                    statement.executeQuery("SELECT * " + " FROM " + getRuleSystemMetaData(ruleSystemName).getTableName());

            if (resultSet != null) {
                rules = convertToRules(resultSet, getRuleSystemMetaData(ruleSystemName));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            close(resultSet, statement, connection);
        }

        return rules;
    }

    @Override
    public Rule saveRule(String ruleSystemName, Rule rule) {
        RuleSystemMetaData metaData = getRuleSystemMetaData(ruleSystemName);

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
            connection = getConnection();
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
            close(resultSet, preparedStatement, connection);
        }
        if (ruleList != null && !ruleList.isEmpty()) {
            return ruleList.get(0);
        }
        return null;
    }

    @Override
    public boolean deleteRule(String ruleSystemName, Rule rule) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            RuleSystemMetaData metaData = getRuleSystemMetaData(ruleSystemName);

            String sql = "DELETE FROM " + metaData.getTableName()
                + " WHERE " + metaData.getUniqueIdColumnName() + "= ?";

            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, rule.getColumnData(metaData.getUniqueIdColumnName()).getRawValue());

            return preparedStatement.executeUpdate() > 0;
        } catch (IOException | SQLException e) {
            throw new DataAccessException("Error in deleting updated rule", e);
        } finally {
            close(null, preparedStatement, connection);
        }
    }

    @Override
    public Rule updateRule(String ruleSystemName, Rule rule) {
        RuleSystemMetaData metaData = getRuleSystemMetaData(ruleSystemName);

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
            connection = getConnection();
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
            close(resultSet, preparedStatement, connection);
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

    @Override
    public void addRuleInput(String ruleSystemName, RuleInputMetaData ruleInput) {
        RuleSystemMetadataMysqlModel metaData = getRuleSystemMetaData(ruleSystemName);

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
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            preparedStatement =
                connection.prepareStatement("SELECT * " + " FROM " + metaData.getTableName());
            resultSet = null;

            // Add rule input
            preparedStatement.executeUpdate(sqlBuilder.toString());
            // Reload data in the metadata map
            metaDataMap.put(ruleSystemName, loadRuleSystemMetaData(ruleSystemName));
        } catch (IOException | SQLException e) {
            throw new DataAccessException("Error saving new rule input", e);
        } finally {
            close(resultSet, preparedStatement, connection);
        }
    }

    @Override
    public void deleteRuleInput(String ruleSystemName, String ruleInputName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            RuleSystemMetadataMysqlModel metaData = getRuleSystemMetaData(ruleSystemName);

            String sql = "DELETE FROM rule_input "
                + " WHERE rule_system_id = " + metaData.getRuleSystemId() + " AND name = ?";

            connection = getConnection();
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, ruleInputName);

            preparedStatement.executeUpdate();
            // Reload data in the metadata map
            metaDataMap.put(ruleSystemName, loadRuleSystemMetaData(ruleSystemName));
        } catch (IOException | SQLException e) {
            throw new DataAccessException("Error in deleting rule input", e);
        } finally {
            close(null, preparedStatement, connection);
        }
    }

    @Override
    public RuleSystemMetadataMysqlModel getRuleSystemMetaData(String ruleSystemName) {
        try {
            RuleSystemMetadataMysqlModel rsMetadataMysqlModel = metaDataMap.get(ruleSystemName);
            if (rsMetadataMysqlModel == null) {
                rsMetadataMysqlModel = loadRuleSystemMetaData(ruleSystemName);
                metaDataMap.put(ruleSystemName, rsMetadataMysqlModel);
            }

            return rsMetadataMysqlModel;
        } catch (IOException | SQLException e) {
            throw new DataAccessException("Error loading rule meta data", e);
        }
    }

    public RuleSystemMetadataMysqlModel loadRuleSystemMetaData(String ruleSystemName) throws IOException, SQLException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
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
                getInputs(ruleSystemName),
                resultSet.getLong("id"));
        } finally {
            close(resultSet, statement, connection);
        }
    }

    private List<RuleInputMetaData> getInputs(String ruleSystemName) throws SQLException, IOException {
        List<RuleInputMetaData> inputs = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
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
            close(resultSet, statement, connection);
        }

        return inputs;
    }

    private void close(ResultSet resultSet, Statement statement, Connection connection) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            throw new DataAccessException("Failed to close database connection", e);
        }
    }
}
