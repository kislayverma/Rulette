package rulesystem.dao;

import java.util.List;
import java.util.Map;

import rulesystem.Rule;

public interface RuleSystemDao {
	Map<String, String> getRuleSystemDetails(String name);
	List<String> getInputs(String ruleSystemName);
	List<Rule> getAllRules(String ruleSystemName);
	Rule saveRule(Rule rule);
}
