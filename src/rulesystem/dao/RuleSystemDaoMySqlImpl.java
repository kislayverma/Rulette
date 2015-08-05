package rulesystem.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rulesystem.Rule;
import rulesystem.ruleinput.RuleInputMetaData;
import rulesystem.ruleinput.RuleType;

public class RuleSystemDaoMySqlImpl implements RuleSystemDao {

    private DataSource dataSource;
    private String tableName;
    private List<RuleInputMetaData> inputColumnList;
    private String uniqueIdColumnName = "id";
    private String uniqueOutputColumnName = "rule_output_id";

    public RuleSystemDaoMySqlImpl(
            String ruleSystemName, String uniqueIdColName, String uniqueOutputColName) throws Exception {
        if (uniqueIdColName != null) {
            this.uniqueIdColumnName = uniqueIdColName;
        }
        if (uniqueOutputColName != null) {
            this.uniqueOutputColumnName = uniqueOutputColName;
        }

        // This will load the MySQL driver
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new Exception(e);
        }

        initDatabaseConnection();
        Map<String, String> rsDetailMap = getRuleSystemDetails(ruleSystemName);
        if (rsDetailMap.isEmpty()) {
            return;
        }

        this.tableName = rsDetailMap.get("table_name");
    }

    @Override
    public boolean isValid() {
        return (this.tableName == null) ? false : true;
    }

    // Source of the copy-paste : http://www.vogella.com/articles/MySQLJava/article.html
    private void initDatabaseConnection() throws SQLException, IOException {
        this.dataSource = DataSource.getInstance();
    }

    @Override
    public Map<String, String> getRuleSystemDetails(String ruleSystemName) throws SQLException, Exception {
        Map<String, String> rsDetailMap = new HashMap<>();

        Statement statement = this.dataSource.getConnection().createStatement();
        ResultSet resultSet =
                statement.executeQuery("SELECT * FROM rule_system WHERE name LIKE '" + ruleSystemName + "'");

        while (resultSet.next()) {
            // It is possible to get the columns via name
            // also possible to get the columns via the column number
            // which starts at 1
            // e.g. resultSet.getSTring(2);
            rsDetailMap.put("id", resultSet.getString("id"));
            rsDetailMap.put("name", resultSet.getString("name"));
            rsDetailMap.put("table_name", resultSet.getString("table_name"));
        }

        return rsDetailMap;
    }

    @Override
    public List<RuleInputMetaData> getInputs(String ruleSystemName) throws SQLException, Exception {
        List<RuleInputMetaData> inputs = new ArrayList<>();

        Statement statement = this.dataSource.getConnection().createStatement();
        ResultSet resultSet =
                statement.executeQuery("SELECT b.* "
                + "FROM rule_system AS a "
                + "JOIN rule_input AS b "
                + "    ON b.rule_system_id = a.id "
                + "WHERE a.name LIKE '" + ruleSystemName + "' "
                + "ORDER BY b.priority ASC ");

        while (resultSet.next()) {
            RuleType ruleType =
                    "Value".equalsIgnoreCase(resultSet.getString("rule_type"))
                    ? RuleType.VALUE : RuleType.RANGE;

            inputs.add(new RuleInputMetaData(resultSet.getInt("id"),
                    resultSet.getInt("rule_system_id"),
                    resultSet.getString("name"),
                    resultSet.getInt("priority"),
                    ruleType,
                    Utils.getRuleInputDataTypeFromName(resultSet.getString("data_type"))));
        }

        this.inputColumnList = inputs;

        return inputs;
    }

    @Override
    public List<Rule> getAllRules(String ruleSystemName) throws SQLException, Exception {
        List<Rule> rules = new ArrayList<>();

        Statement statement = this.dataSource.getConnection().createStatement();
        ResultSet resultSet =
                statement.executeQuery("SELECT * " + " FROM " + this.tableName);

        if (resultSet != null) {
            rules = convertToRules(resultSet);
        }

        return rules;
    }

    private List<Rule> convertToRules(ResultSet resultSet) throws Exception {
        List<Rule> rules = new ArrayList<>();

        if (resultSet != null) {
            while (resultSet.next()) {
                Map<String, String> inputMap = new HashMap<>();

                for (RuleInputMetaData col : this.inputColumnList) {
                    inputMap.put(col.getName(), resultSet.getString(col.getName()));
                }
                inputMap.put(this.uniqueIdColumnName,
                        resultSet.getString(this.uniqueIdColumnName));
                inputMap.put(this.uniqueOutputColumnName,
                        resultSet.getString(this.uniqueOutputColumnName));

                rules.add(new Rule(this.inputColumnList, inputMap, this.uniqueIdColumnName, this.uniqueOutputColumnName));
            }
        }

        return rules;
    }

    @Override
    public Rule saveRule(Rule rule) throws SQLException, Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder nameListBuilder = new StringBuilder();
        StringBuilder valueListBuilder = new StringBuilder();

        for (RuleInputMetaData col : this.inputColumnList) {
            nameListBuilder.append(col.getName()).append(",");
            String val = rule.getColumnData(col.getName()).getRawValue();
            valueListBuilder.append(val.isEmpty() ? null : "'" + val + "'").append(",");
        }
        nameListBuilder.append(this.uniqueOutputColumnName).append(",");
        valueListBuilder.append(rule.getColumnData(this.uniqueOutputColumnName).getRawValue()).append(",");

        sqlBuilder.append("INSERT INTO ")
                .append(this.tableName)
                .append(" (").append(nameListBuilder.toString().substring(0, nameListBuilder.length() - 1)).append(") ")
                .append(" VALUES (").append(valueListBuilder.toString().substring(0, valueListBuilder.length() - 1)).append(") ");

        Connection connection = this.dataSource.getConnection();
        PreparedStatement preparedStatement =
                connection.prepareStatement("SELECT * " + " FROM " + this.tableName);

        if (preparedStatement.executeUpdate(
                sqlBuilder.toString(), Statement.RETURN_GENERATED_KEYS) > 0) {
            // Get the rule object for returning using LAST_INSERT_ID() MySql function.
            // This id is maintained per connection so multiple instances inserting rows
            // isn't a problem.
            preparedStatement =
                    connection.prepareStatement("SELECT * FROM " + this.tableName
                    + " WHERE " + this.uniqueIdColumnName
                    + " = LAST_INSERT_ID()");
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Rule> ruleList = convertToRules(resultSet);
            if (ruleList != null && !ruleList.isEmpty()) {
                return ruleList.get(0);
            }
        }

        return null;
    }

    @Override
    public boolean deleteRule(Rule rule) throws SQLException, Exception {
        String sql = "DELETE FROM " + this.tableName
                + " WHERE " + this.uniqueIdColumnName + "= ?";
        PreparedStatement preparedStatement = this.dataSource.getConnection().prepareStatement(sql);
        preparedStatement.setString(1, rule.getColumnData(this.uniqueIdColumnName).getRawValue());

        if (preparedStatement.executeUpdate() > 0) {
            return true;
        }

        return false;
    }

    @Override
    public Rule updateRule(Rule rule) throws SQLException, Exception {
        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder updateListBuilder = new StringBuilder();

        for (RuleInputMetaData col : this.inputColumnList) {
            String val = rule.getColumnData(col.getName()).getRawValue();

            updateListBuilder.append(col.getName())
                    .append("=")
                    .append("".equals(val) ? null : "'" + val + "'")
                    .append(",");
        }
        updateListBuilder.append(this.uniqueOutputColumnName)
                .append("=")
                .append(rule.getColumnData(this.uniqueOutputColumnName).getRawValue())
                .append(",");

        String oldRuleId = rule.getColumnData(this.uniqueIdColumnName).getRawValue();
        sqlBuilder.append("UPDATE ")
                .append(this.tableName)
                .append(" SET ")
                .append(updateListBuilder.toString().substring(0, updateListBuilder.length() - 1))
                .append(" WHERE ")
                .append(this.uniqueIdColumnName)
                .append("=")
                .append(oldRuleId);

        Connection connection = this.dataSource.getConnection();
        PreparedStatement preparedStatement =
                connection.prepareStatement(sqlBuilder.toString());
        if (preparedStatement.executeUpdate() > 0) {
            preparedStatement =
                    connection.prepareStatement("SELECT * FROM " + this.tableName
                    + " WHERE " + this.uniqueIdColumnName
                    + "=" + oldRuleId);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Rule> ruleList = convertToRules(resultSet);
            if (ruleList != null && !ruleList.isEmpty()) {
                return ruleList.get(0);
            }
        }

        return null;
    }
}
