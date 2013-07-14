package rulesystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import rulesystem.dao.RuleSystemDao;
import rulesystem.dao.RuleSystemDaoMySqlImpl;
import rulesystem.validator.DefaultValidator;
import rulesystem.validator.Validator;

/**
 * This class models a rule-system comprising of rules and provides appropriate APIs to interact with it.
 * A rule-system is a generic representation of an input-output mapping, with multiple input fields 
 * mapping to a single output field.
 * 
 * The exposed APIs are:
 * List<Rule> getAllRules()
 * Rule getRule(Integer rule_id)
 * Rule getRule(Map<String, String>)
 * Rule addRule(Rule)
 * Rule addRule(Map<String, String>)
 * Rule deleteRule(Rule)
 * Rule deleteRule(Integer rule_id)
 * List<Rule> getConflictingRules(Rule)
 * Rule getNextApplicableRule(Map<String, String>)
 * 
 * @author Kislay Verma
 *
 */
public class RuleSystem {
    private final Validator validator;
    private RuleSystemDao dao;
    private String name;

    private List<Rule> allRules;

    // This list is to keep the order (priority order) of inputs
    private List<String> inputColumnList;

    public static final String UNIQUE_ID_COLUMN_NAME = "rule_id";
    public static final String UNIQUE_OUTPUT_COLUMN_NAME = "rule_output_id";

    /*
     * This class is used to sort lists of eligible rules to get the best fitting rule.
     * The sort also helps in determining the next applicable rule. It is not meant as 
     * a general rule comparator as that does not make any sense at all (which is also why
     * the Rule class does not implement Comparable - it would suggest that, in general, 
     * rules can be compared against each other for priority ordering or whatever).
     * 
     * The comparator iterates over the input fields in decreasing order of priority and ranks
     * a specific value higher than 'Any'.
     */
    private class RuleComparator implements Comparator<Rule> {

        @Override
        public int compare(Rule rule1, Rule rule2) {
            for (String colName : inputColumnList) {
        		if (colName.equals(RuleSystem.UNIQUE_ID_COLUMN_NAME) ||
            		colName.equals(RuleSystem.UNIQUE_OUTPUT_COLUMN_NAME))
            	{
            		continue;
            	}

        		String colValue1 = rule1.getValueForColumn(colName);
                colValue1 = (colValue1 == null) ? "" : colValue1;
                String colValue2 = rule1.getValueForColumn(colName);
                colValue2 = (colValue2 == null) ? "" : colValue2;

                /*
                 *  In going down the order of priority of inputs, the first mismatch will yield the 
                 *  answer of the comparison. "" (meaning 'Any') matches everything, but an exact match
                 *  is better. So if the column values are unequal, whichever rule has non-'Any' as the 
                 *  value will rank higher.
                 */
                if (! colValue1.equals(colValue2)) {
                	return ("".equals(colValue1)) ? -1 : 1;
                }
            }

            // If all column values are same
            return 0;
        }
    }

    /**
     * This constructor accepts a path to a text file containing the following values on separate lines:
     * 1. Name of the rule system
     * 2. Full path of the file containing the rules
     * 
     * @param ruleSystemName
     * @param validator
     */
    public RuleSystem(String ruleSystemName, Validator validator) {
    	this.name = ruleSystemName;
        this.validator = (validator != null) ? validator : new DefaultValidator();
        this.dao = new RuleSystemDaoMySqlImpl(ruleSystemName);

        initRuleSystem(ruleSystemName);
    }

    /**
     * This method returns a list of all the rules in the rule system (for display purposes etc.)
     * The returned list is a copy of the actual rules to prevent any accidental modification of 
     * rule outside the system from impacting the working of the rule  system.
     * 
     */
    public List<Rule> getAllRules() {
        // Return rules defensively so they won't be modified accidentally otside the system
        List<Rule> allRuleClone = new ArrayList<Rule>();
        Collections.copy(allRuleClone, this.allRules);

        return allRuleClone;
    }

    /**
     * This method returns the applicable rule for the given input criteria.
     * 
     * @param inputMap Map with column Names as keys and column values as values.
     * @return A {@link Rule} object if a rule is applicable.
     *         null otherwise.
     */
    public Rule getRule(Map<String, String> inputMap) {
        List<Rule> eligibleRules = getEligibleRules(inputMap);
        if (! eligibleRules.isEmpty()) {
            return eligibleRules.get(0);
        }

        return null;
    }

    /**
     * This method returns the applicable rule for the given input criteria.
     * 
     * @param ruleId Unique id of the rule to get looked up.
     * @return A {@link Rule} object if a rule with the given id exists.
     *         null otherwise.
     */
    public Rule getRule(Integer ruleId) {
    	for (Rule rule : this.allRules) {
    		Integer id = Integer.parseInt(rule.getValueForColumn(UNIQUE_ID_COLUMN_NAME));
    		if (id.equals(ruleId)) {
    			return rule;
    		}
    	}

        return null;
    }

    /**
     * This method adds a new rule to the rule system. There is no need to provide the rule_id 
     * field in the input - it will be auto-populated. 
     * 
     * @param inputMap
     * @return the added rule if there are no overlapping rules
     *         null if there are overlapping rules
     *         null if the input constitutes an invalid rule as per the validation policy in use.
     */
    public Rule addRule(Map<String, String> inputMap) {
    	Rule newRule = new Rule(this.inputColumnList, inputMap);
    	return addRule(newRule);
    }

    /**
     * This method adds the given rule to the rule system with a new rule id.
     * 
     * @param newRule
     * @return the added rule if there are no overlapping rules
     *         null if there are overlapping rules
     *         null if the input constitutes an invalid rule as per the validation policy in use.
     */
    public Rule addRule(Rule newRule) {
    	if (! this.validator.isValid(newRule)) {
        	System.err.println("Invalid input.");
    		return null;
    	}

    	List<Rule> overlappingRules = getConflictingRules(newRule);
    	if (overlappingRules.isEmpty()) {
    		newRule = dao.saveRule(newRule);
    		if (newRule != null) {
        		// Cache the rule
        		this.allRules.add(newRule);
        		return newRule;
    		}
    	}

		return null;
    }

    /**
     * This method deletes an existing rule from the rule system.
     * 
     * @param ruleId Unique id of the rule to be deleted
     * @return true if the rule with given rule id was successfully deleted
     *         false if the given rule does not exist
     *         false if the given rule could not be deleted (for whatever reason).
     */
    public boolean deleteRule(Integer ruleId) {
    	if (ruleId != null) {
        	Rule rule = getRule(ruleId);
        	return deleteRule(rule);
    	}

    	return false;
    }

    /**
     * This method deleted the given rule from the rule system.
     * 
     * @param rule The {@link Rule} to be deleted.
     * @return true if the given rule was successfully deleted
     *         false if the given rule does not exist
     *         false if the given rule could not be deleted (for whatever reason).
     */

    public boolean deleteRule(Rule rule) {
		boolean status = dao.deleteRule(rule);
		if (status) {
			List<Rule> newList = new ArrayList<>();
    		// Remove the rule from the cache
			for (Rule r : this.allRules) {
				if (! r.getValueForColumn(UNIQUE_ID_COLUMN_NAME).equals(rule.getValueForColumn(UNIQUE_ID_COLUMN_NAME))) {
					newList.add(r);
				}
			}
    		this.allRules = newList;

    		return true;
		}

		return false;
    }

    /**
     * This method returns a list of rules conflicting with the given rule.
     * 
     * @param rule {@link Rule} object
     * @return List of conflicting rules if any, empty list otherwise.
     */
    public List<Rule> getConflictingRules(Rule rule) {
    	List<Rule> conflictingRules = new ArrayList<Rule>();
    	Comparator<Rule> ruleComp = new RuleComparator();

    	for (Rule r : this.allRules) {
    		if (ruleComp.compare(r, rule) == 0) {
    			conflictingRules.add(r);
    		}
    	}

    	return conflictingRules;
    }

    /**
     * This method returns the next rule that will be applicable to the inputs if 
     * the current rule applicable to the were to be deleted.
     * 
     * @param inputMap Map with column Names as keys and column values as values.
     * @return A {@link Rule} object if a rule is applicable after the currently 
     *         applicable rule  is deleted.
     *         null if no  rule  is applicable after the currently applicable rule is deleted.
     *         null id no rule is  currently applicable.
     */
    public Rule getNextApplicableRule(Map<String, String> inputMap) {
        List<Rule> eligibleRules = getEligibleRules(inputMap);

        if (eligibleRules.size() > 2) {
            return eligibleRules.get(1);
        }

        return null;
    }

    private List<Rule> getEligibleRules(Map<String, String> inputMap) {
        // Start fresh
        List<Rule> eligibleRules = new ArrayList<Rule>();
        for (Rule rule : allRules) {
            if (rule.evaluate(inputMap)) {
                eligibleRules.add(rule);
            }
        }

        Collections.sort(eligibleRules, new RuleComparator());
        System.out.println(eligibleRules.size() + " eligible rules.");

        return eligibleRules;
    }

    /*
     * 1. Get rule system inputs from rule_system..rule_input table.
     * 2. Get rules from the table specified for this rule system in the 
     *    rule_system..rule_system table
     */
    private void initRuleSystem(String ruleSystemName) {
    	this.inputColumnList = dao.getInputs(ruleSystemName);

    	List<Rule> rules = dao.getAllRules(ruleSystemName);
        for (Rule rule : rules) {
            if (this.validator.isValid(rule)) {
            	if (this.allRules == null) {
            		this.allRules = new ArrayList<>();
            	}

            	this.allRules.add(rule);
            }
        }
    }

    public String getName() {
    	return this.name;
    }

    public static void main(String[] args) {
    	RuleSystem rs = new RuleSystem("discount_rule_system", null);
    }
}
