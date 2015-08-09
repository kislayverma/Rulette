package rulesystem.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import rulesystem.dao.MetaDataDao;
import rulesystem.metadata.RuleSystemMetaData;
import rulesystem.ruleinput.RuleInputMetaData;
import rulesystem.ruleinput.RuleType;
import rulesystem.ruleinput.value.InputDataType;

public class MetaDataDaoMySqlImpl extends BaseDaoMySqlImpl implements MetaDataDao {

    public MetaDataDaoMySqlImpl() throws Exception {
        super();
    }

    @Override
    public RuleSystemMetaData getRuleSystemMetaData(String ruleSystemName) throws Exception {
        Statement statement = dataSource.getConnection().createStatement();
        ResultSet resultSet =
                statement.executeQuery("SELECT * FROM rule_system WHERE name LIKE '" + ruleSystemName + "'");

        RuleSystemMetaData metaData = new RuleSystemMetaData(
            resultSet.getString("name"),
            resultSet.getString("table_name"),
            resultSet.getString("uniqueIdColumnName"),
            resultSet.getString("uniqueOutputColumnName"),
            getInputs(ruleSystemName));

        return metaData;
    }

    private List<RuleInputMetaData> getInputs(String ruleSystemName) throws SQLException, Exception {
        List<RuleInputMetaData> inputs = new ArrayList<>();

        Statement statement = dataSource.getConnection().createStatement();
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
            InputDataType dataType = InputDataType.valueOf(resultSet.getString("data_type"));

            inputs.add(new RuleInputMetaData(resultSet.getInt("id"),
                    resultSet.getInt("rule_system_id"),
                    resultSet.getString("name"),
                    resultSet.getInt("priority"),
                    ruleType,
                    dataType));
        }

        return inputs;
    }
}
