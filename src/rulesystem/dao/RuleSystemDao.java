package rulesystem.dao;

import java.util.List;
import java.util.Map;

import rulesystem.Rule;
import rulesystem.ruleinput.RuleInputMetaData;

public interface RuleSystemDao {
	Map<String, String> getRuleSystemDetails(String name);
	List<RuleInputMetaData> getInputs(String ruleSystemName) throws Exception;
	List<Rule> getAllRules(String ruleSystemName) throws Exception;
	Rule saveRule(Rule rule);
	boolean deleteRule(Rule rule);
	boolean isValid();
}
