package rulette.dao;

import java.sql.SQLException;
import java.util.List;
import rulette.rule.Rule;

public interface RuleSystemDao {
    List<Rule> getAllRules(String ruleSystemName) throws SQLException, Exception;

    Rule saveRule(String ruleSystemName, Rule rule) throws SQLException, Exception;

    boolean deleteRule(String ruleSystemName, Rule rule) throws SQLException, Exception;

    Rule updateRule(String ruleSystemName, Rule rule) throws SQLException, Exception;
}
