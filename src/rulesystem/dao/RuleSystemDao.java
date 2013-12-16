package rulesystem.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import rulesystem.Rule;
import rulesystem.ruleinput.RuleInputMetaData;

public interface RuleSystemDao {

    Map<String, String> getRuleSystemDetails(String name) throws SQLException, Exception;

    List<RuleInputMetaData> getInputs(String ruleSystemName) throws SQLException, Exception;

    List<Rule> getAllRules(String ruleSystemName) throws SQLException, Exception;

    Rule saveRule(Rule rule) throws SQLException, Exception;

    boolean deleteRule(Rule rule) throws SQLException, Exception;

    boolean isValid();

    Rule updateRule(Rule rule) throws SQLException, Exception;
}
