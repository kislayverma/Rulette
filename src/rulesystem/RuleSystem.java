package rulesystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import rulesystem.dao.RuleSystemDao;
import rulesystem.dao.RuleSystemDaoMySqlImpl;
import rulesystem.ruleinput.RuleInputMetaData;
import rulesystem.ruleinput.RuleInputMetaData.DataType;
import rulesystem.validator.DefaultValidator;
import rulesystem.validator.Validator;

/**
 * This class models a rule-system comprising of rules and provides appropriate APIs to interact 
 * with it.
 * 
 * A rule-system, in this context, is a mapping of elements of an input space comprising of one 
 * or more distinct inputs to a well-defined output space. This is a generic implementation which 
 * allows for creation and management of these mappings. Much can be read about rule-systems 
 * elsewhere (Drools is a particularly well known and elaborate implementation), so I will just 
 * lay out the specifics of this particular implementation:
 * 
 * 1. This is a lightweight, easy to setup implementation, agnostic to the input and 
 *    output domains. The offered APIs deal only with mappings (henceforth called rules) and take 
 *    no cognizance of what the inputs and output mean. This is by design. To use this in an 
 *    application, I would expect that you would wrap this core engine with a module which 
 *    understands the semantics of your application.
 * 2. An example of a rule would be If X= 2 AND Y = 3, THEN Z =42. To match any value of an input,
 *    just pass null, like so : If X= null AND Y = 3, THEN Z = 51
 * 3. A 'rule input' is a criterion, which in combination with other of its kind, decides an 
 *    outcome. In #2, X and Y are rule inputs.
 * 4. 2 types of rule input are supported : 'Value' and 'Range'. Value inputs are discrete valued 
 *    criteria, while range inputs define ranges in the input space.
 * 5. Only 'AND' operation between the rule inputs is supported.
 * 6. All rule inputs are treated as strings. The ranges defined by range inputs are also 
 *    interpreted as string ranges. This might require you to invest some thought into how you want
 *    to model your rules. e.g. To have a date range an an input, then a possible way to
 *    specify it as CCYYMMDD representations of the start and end dates. This defines a range just
 *    as well as actual dates.
 * 7. Input have a priority order. This is the order in which they are evaluated to arrive at the 
 *    output. Defining priorities is much like defining database indexes - different choices can 
 *    cause widely divergent performance. Worse-depending on your domain, incorrect priorities may 
 *    even lead to incorrect results.
 * 8. Rules are captured in database tables (one per rule system). These tables must have two 
 *    columns : 'rule_id' (unique id for the rule, preferable an auto-incrementing primary key)
 *    and 'rule_output_id' (unique identifier for the output). The engine doesn't care what you do 
 *    with the rule_output_id. It is simply what the inputs map to. It may be a 
 *    foreign key reference to another table . It may be the actual value you need. It simply 
 *    doesn't matter to this system. The other columns each represent an input.
 * 9. To do rule evaluation, the system takes the combination of the different rule inputs given 
 *    to it, and returns the best fitting rule (if any). 'Best fit' means:
 *    a. Value inputs - An exact value match is better than an 'any' match. e.g. if there are two 
 *       rules, one with value of input X as 1 and the other as any, then on passing X = 1, the 
 *       former rule will be returned. On passing X = 2, the latter will be returned (as the 
 *       former obviously doesn't match).
 *    b. Range inputs : A tighter range is a better fit than a wider range. e.g. if there are two 
 *       rules, one with value of input X as Jan 1 2013 to Dec31, 2013 and the other as Feb 1 2013 
 *       to March 1 2013, then on passing X = Feb 15, 2013, the latter will be returned.
 * 10. Conflicting rules are those that will, if present in the system, cause ambiguity at the time 
 *     of rule evaluation. The addRule APIs provided do not allow addition of conflicting rules.
 * 
 * The following APIs are exposed for interacting with the rule system:
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
 * Pre-requisites:
 * ---------------
 * 1. Java 1.7
 * 2. MySQL 5.x (Support for other databases will be added if I see anyone actually giving a F*** 
 *    about that).
 * 
 * How  to setup:
 * --------------
 * 1. Execute the setup.sql script on your MySQL server. This creates a database called rule_system
 *    and creates the necessary table in it.
 * 2. Create a table containing your rules as defined in #7 above (if you don't have it already).
 * 3. Map this table in the rule_system.rule_system table as shown in the sample-0setup.sql script.
 * 4. For each rule input, add a row to the rule_system.rule_input table with the input's type 
 *    (Value/Range) and priority order.
 * 5. Put the jar in your class path.
 * 
 * That's  it! The rule system is all set up and ready to use.
 * 
 * Sample usage
 * ------------
 * RuleSystem rs = new RuleSystem(<rule system name as configured>[, <validator>]);
 * Rule r = rs.getRule(<ruleid>);
 * 
 * @author Kislay Verma
 *
 */
public class RuleSystem {
    private final Validator validator;
    private RuleSystemDao dao;
    private String name;

    private Map<Integer, Rule> allRules;
    private RSNode root;

    // This list is to keep the order (priority order) of inputs
    private List<RuleInputMetaData> inputColumnList;

    private String uniqueIdColumnName = "id";
    private String uniqueOutputColumnName = "rule_output_id";

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
            for (RuleInputMetaData col : inputColumnList) {
            	String colName = col.getName();

            	if (colName.equals(uniqueIdColumnName) ||
            		colName.equals(uniqueOutputColumnName))
            	{
            		continue;
            	}

        		String colValue1 = rule1.getColumnData(colName).getValue();
                colValue1 = (colValue1 == null) ? "" : colValue1;
                String colValue2 = rule2.getColumnData(colName).getValue();
                colValue2 = (colValue2 == null) ? "" : colValue2;

                /*
                 *  In going down the order of priority of inputs, the first mismatch will 
                 *  yield the answer of the comparison. "" (meaning 'Any') matches everything,
                 *  but an exact match is better. So if the column values are unequal, whichever 
                 *  rule has non-'Any' as the value will rank higher.
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
     * This constructor accepts a path to a text file containing the following values on 
     * separate lines:
     * 1. Name of the rule system
     * 2. Full path of the file containing the rules
     * 
     * @param ruleSystemName
     * @param validator
	 * @param uniqueIdColName [OPTIONAL] Name of the column containing unique id for the rule.
	 *                        "id" will be used by default.
	 * @param uniqueOutputColName [OPTIONAL] Name of the column containing the output of the rule 
	 *                            system. "rule_output_id" will be used by default.
     * @throws Exception 
     */
    public RuleSystem(String ruleSystemName, String uniqueIdColName, String uniqueOutputColName, Validator validator) throws Exception {
    	this.name = ruleSystemName;
    	if (uniqueIdColName != null) {
        	this.uniqueIdColumnName = uniqueIdColName;
    	}
    	if (uniqueOutputColName != null) {
        	this.uniqueOutputColumnName = uniqueOutputColName;
    	}
        this.validator = (validator != null) ? validator : new DefaultValidator();
        this.dao = new RuleSystemDaoMySqlImpl(ruleSystemName, uniqueIdColName, uniqueOutputColName);
        if (! this.dao.isValid()) {
        	throw new RuntimeException("The rule system with name " + ruleSystemName +
        			                   " could not be initialized");
        }

        initRuleSystem(ruleSystemName);
    }

    /**
     * This method returns a list of all the rules in the rule system.
     */
    public List<Rule> getAllRules() {
    	return new ArrayList<>(this.allRules.values());
    }

    /**
     * This method returns the rule applicable for the given combination of rule inputs.
     * 
     * @param inputMap Map with input names as keys and their String values as values
     * @return null if input is null
     *         null if no rule  is applicable for the given inout combination
     *         the applicable rule otherwise.
     */
    public Rule getRule(Map<String, String> inputMap) {
    	List<Rule> eligibleRules = getEligibleRules(inputMap);
    	if (eligibleRules != null && ! eligibleRules.isEmpty()) {
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
    	if (ruleId == null) {
    		return null;
    	}

    	return this.allRules.get(ruleId);
    }

    /**
     * This method adds a new rule to the rule system. There is no need to provide the rule_id 
     * field in the input - it will be auto-populated. 
     * 
     * @param inputMap
     * @return the added rule if there are no overlapping rules
     *         null if there are overlapping rules
     *         null if the input constitutes an invalid rule as per the validation policy in use.
     * @throws Exception 
     */
    public Rule addRule(Map<String, String> inputMap) throws Exception {
    	if (inputMap == null) {
    		return null;
    	}

    	Rule newRule = new Rule(this.inputColumnList, inputMap, this.uniqueIdColumnName, this.uniqueOutputColumnName);
    	return addRule(newRule);
    }

    /**
     * This method adds the given rule to the rule system with a new rule id.
     * 
     * @param newRule
     * @return the added rule if there are no overlapping rules
     *         null if there are overlapping rules
     *         null if the input constitutes an invalid rule as per the validation policy in use.
     * @throws Exception 
     */
    public Rule addRule(Rule newRule) throws Exception {
    	if (newRule == null || ! this.validator.isValid(newRule)) {
    		return null;
    	}

    	String ruleOutputId = newRule.getColumnData(this.uniqueOutputColumnName).getValue();
    	if (ruleOutputId == null || ruleOutputId.isEmpty()) {
    		throw new RuntimeException("Rule can't be saved without rule_output_id.");
    	}

    	List<Rule> overlappingRules = getConflictingRules(newRule);
    	if (overlappingRules.isEmpty()) {
    		newRule = dao.saveRule(newRule);
    		if (newRule != null) {
        		// Cache the rule
    			addRuleToCache(newRule);

    			return newRule;
    		}
    	}

    	throw new RuntimeException("The following existing rules conflict with " +
    	                           "the given input : " + overlappingRules);
    }

    /**
     * This method updates an existing rules with values of the new rule given. All fields are 
     * updated of the old rule are updated. The new rule is checked for conflicts before update.
     * 
     * @param oldRule An existing rule
     * @param newRule The rule containing the new field values to which the old rule will be 
     *                updated.
     * @return the updated rule if update creates no conflict.
     *         null if the input constitutes an invalid rule as per the validation policy in use.
     * @throws Exception if there are overlapping rules
     *                   if the old rules does not actually exist.
     */
    public Rule updateRule(Rule oldRule, Rule newRule) throws Exception {
    	if (oldRule == null || newRule == null || ! this.validator.isValid(newRule)) {
    		return null;
    	}

    	String oldRuleId = oldRule.getColumnData(this.uniqueIdColumnName).getValue();
    	Rule checkForOldRule = this.getRule(Integer.parseInt(oldRuleId));
    	if (checkForOldRule == null) {
    		throw new Exception("No existing rule with id " + oldRuleId);
    	}

    	List<Rule> overlappingRules = getConflictingRules(newRule);
    	if (! overlappingRules.isEmpty()) {
    		boolean otherOverlappingRules = false;
    		for (Rule overlappingRule : overlappingRules) {
    			if (! overlappingRule.getColumnData(uniqueIdColumnName).getValue()
    					.equals(oldRuleId))
    			{
    				otherOverlappingRules = true;
    			}
    		}

    		if (otherOverlappingRules) {
    	    	throw new RuntimeException("The following existing rules conflict with " +
                        "the given input : " + overlappingRules);
    		}
    	}

		Rule resultantRule = dao.updateRule(newRule);
		if (resultantRule != null) {
			deleteRuleFromCache(oldRule);
			addRuleToCache(resultantRule);
		}

		return resultantRule;
    }

    /**
     * This method deletes an existing rule from the rule system.
     * 
     * @param ruleId Unique id of the rule to be deleted
     * @return true if the rule with given rule id was successfully deleted
     *         false if the given rule does not exist
     *         false if the given rule could not be deleted (for whatever reason).
     * @throws Exception 
     */
    public boolean deleteRule(Integer ruleId) throws Exception {
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
     * @throws Exception 
     */

    public boolean deleteRule(Rule rule) throws Exception {
    	if (rule == null) {
    		return false;
    	}

    	boolean status = dao.deleteRule(rule);
		if (status) {
    		// Remove the rule from the cache
			deleteRuleFromCache(rule);

    		return true;
		}

		return false;
    }

    /**
     * This method returns a list of rules conflicting with the given rule.
     * 
     * @param rule {@link Rule} object
     * @return List of conflicting rules if any, empty list otherwise.
     * @throws Exception 
     */
    public List<Rule> getConflictingRules(Rule rule) throws Exception {
    	if (rule == null) {
    		return null;
    	}
    	List<Rule> conflictingRules = new ArrayList<Rule>();

    	for (Rule r : this.allRules.values()) {
    		if (r.isConflicting(rule)) {
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
     *         null id no rule is currently applicable.
     */
    public Rule getNextApplicableRule(Map<String, String> inputMap) {
        List<Rule> eligibleRules = getEligibleRules(inputMap);

        if (eligibleRules != null && eligibleRules.size() > 1) {
    		return eligibleRules.get(1);
    	}

    	return null;
    }

    private List<Rule> getEligibleRules(Map<String, String> inputMap) {
    	if (inputMap != null) {
    		Stack<RSNode> currStack = new Stack<>();
    		currStack.add(root);

    		for (RuleInputMetaData rimd : this.inputColumnList) {
        		Stack<RSNode> nextStack = new Stack<>();
    			for (RSNode node : currStack) {
        			String value = inputMap.get(rimd.getName());
        			value = (value == null) ? "" : value;

        			List<RSNode> eligibleRules = node.getNodes(value, true);
        			if (eligibleRules != null && !eligibleRules.isEmpty()) {
        				nextStack.addAll(eligibleRules);
        			}
    			}
    			currStack = nextStack;
    		}

    		if (! currStack.isEmpty()) {
    			List<Rule> rules = new ArrayList<>();
    			for (RSNode node : currStack) {
    				if (node.getRule() != null) {
        				rules.add(node.getRule());
    				}
    			}

    	        Collections.sort(rules, new RuleComparator());
        		return rules;
    		}
    	}

        return null;
    }

    /*
     * 1. Get rule system inputs from rule_system..rule_input table.
     * 2. Get rules from the table specified for this rule system in the 
     *    rule_system..rule_system table
     */
    private void initRuleSystem(String ruleSystemName) throws Exception {
    	this.inputColumnList = dao.getInputs(ruleSystemName);

    	List<Rule> rules = dao.getAllRules(ruleSystemName);
    	System.out.println("Rules from DB : " + rules.size());

    	this.allRules = new HashMap<>();
    	if (this.inputColumnList.get(0).getDataType().equals(DataType.VALUE)) {
    		this.root = new ValueRSNode(this.inputColumnList.get(0).getName());
    	}
    	else {
    		this.root = new RangeRSNode(this.inputColumnList.get(0).getName());
    	}

    	for (Rule rule : rules) {
    		if (this.validator.isValid(rule)) {
    			addRuleToCache(rule);
            }
        }
    }

    private void addRuleToCache(Rule rule) {
    	RSNode currNode = this.root;
		for (int i = 0;i < this.inputColumnList.size(); i++) {
			RuleInputMetaData currInput = this.inputColumnList.get(i);

			// 1. See if the current node has a node mapping to the field value
    		List<RSNode> nodeList =
    			currNode.getNodes(rule.getColumnData(currInput.getName()).getValue(), false);

    		// 2. If it doesn't, create a new empty node and map the field value 
    		//    to the new node.
    		//    Also move to the new node.
    		if (nodeList.isEmpty()) {
    			RSNode newNode;
    			if (i < this.inputColumnList.size() - 1) {
        			if (this.inputColumnList.get(i + 1).getDataType().equals(DataType.VALUE))
        			{
        				newNode = new ValueRSNode(this.inputColumnList.get(i + 1).getName());
        			}
        			else {
        				newNode = new RangeRSNode(this.inputColumnList.get(i + 1).getName());
        			}
    			}
    			else {
    				newNode = new ValueRSNode("");
    			}

    			currNode.addChildNode(
    				rule.getColumnData(currInput.getName()), newNode);
    			currNode = newNode;
    		}
    		// 3. If it does, move to that node.
    		else {
    			currNode = nodeList.get(0);
    		}
		}

		currNode.setRule(rule);
		this.allRules.put(
			Integer.parseInt(rule.getColumnData(uniqueIdColumnName).getValue()), rule);
    }

    private void deleteRuleFromCache(Rule rule) throws Exception {
    	// Delete the rule from the map
    	this.allRules.remove(
    		Integer.parseInt(rule.getColumnData(uniqueIdColumnName).getValue()));

		// Locate and delete the rule from the trie
    	Stack<RSNode> stack = new Stack<>();
		RSNode currNode = this.root;

		for (RuleInputMetaData rimd : this.inputColumnList) {
			String value = rule.getColumnData(rimd.getName()).getValue();
			value = (value == null) ? "" : value;

			RSNode nextNode = currNode.getMatchingRule(value);
			stack.push(currNode);

			currNode = nextNode;
		}

		if (! currNode.getRule().getColumnData(uniqueIdColumnName).equals(
				rule.getColumnData(uniqueIdColumnName))) {
			throw new Exception("The rule to be deleted and the rule found are not the same." +
					            "Something went horribly wrong");
		}

		// Get rid of the leaf node
		stack.pop();
		currNode = null;

		// Handle the ancestors of the leaf
		while (! stack.isEmpty()) {
			RSNode node = stack.pop();

			// Visit nodes in leaf to root order and:
			// 1. If this is the only value in the popped node, delete the node.
			// 2. If there are other values too, remove this value from the node.
    		if (node.getCount() <= 1) {
    			node = null;
			}
    		else {
    			node.removeChildNode(rule.getColumnData(node.getName()));
    		}
		}
    }

    public String getName() {
    	return this.name;
    }

    /**
     * Use this method to set the dao to be used by the Rule System. This is optional as the rule
     * has a default implementation of all database operations.
     * 
     * Inserting your custom dao is a big responsibility that must not be taken up lightly. You can
     * ,however, use this facility in case you must work with pre-existing database systems or to
     * integrate with frameworks like Hibernate.
     * 
     * @param dao
     */
    public void setRuleSystemDao(RuleSystemDao dao) {
    	this.dao = dao;
    }

//    public static void main(String[] args) throws Exception {
//    	long stime = new Date().getTime();
//    	RuleSystem rs = null;
//		try {
//			rs = new RuleSystem("discount_rule_system", "rule_id", "rule_output_id", null);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//    	long etime = new Date().getTime();
//    	System.out.println("Time taken to init rule system : " + (etime-stime));
//
//		//List<Rule> rules = rs.getAllRules();
//    	//System.out.println("The are " + rules.size() + " rules.");
//    	//Rule rule = rs.getRule(1);
//    	//System.out.println("Rule : " + ((rule == null) ? "no rule" : rule.toString()));
//    	Map<String, String> inputMap = new HashMap<>();
//    	//inputMap.put("brand", "lee");
//    	//inputMap.put("article_type", "T Shirt");
//    	inputMap.put("style_id", "0");
//    	inputMap.put("is_active", "1");
//    	//inputMap.put("year", "2013");
////    	long sec = new Date().getTime()/1000;
//    	inputMap.put("valid_date_range", "1321468201");
//    	Rule rule = null;
//    	//rule = rs.getRule(inputMap);
//    	//rs.deleteRule(rule);
//		//System.out.println(rule);
//		//List<Rule> rules = rs.getConflictingRules(rule);
//		//System.out.println(rules);
//    	stime = new Date().getTime();
//    	for (int i = 0; i < 1; i++) {
//        	rule = rs.getRule(inputMap);
//        	//System.out.println((rule == null) ? "none" : rule.toString());
//        	if (rule != null) {
//            	Rule n = rule.setColumnData("style_id", "4420");
//        		//Rule z = rs.getRule(Integer.parseInt(x.getColumnData("rule_id").getValue()));
//        		System.out.println(rule);
//        		System.out.println(n);
//            	rs.updateRule(rule, n);
//        	}
//    		//rule = rs.getRule(4);
//        	//rs.deleteRule(rule);
//        	//rule = rs.getRule(inputMap);
//        	//System.out.println((rule == null) ? "none" : rule.toString());
//        	//inputMap.put("valid_date_range", "1321468200-1357064940");
//        	//inputMap.put("rule_output_id", "872");
//        	//rule = rs.addRule(inputMap);
//        	//rule = rs.getRule(inputMap);
//        	//System.out.println((rule == null) ? "none" : rule.toString());
//    		//rs.getConflictingRules(rule);
//    		//System.out.println(rule);
//    	}
//    	etime = new Date().getTime();
//    	System.out.println("Time taken : " + (etime-stime));
//    	System.out.println((rule == null) ? "none" : rule.toString());
////
////    	Map<String, String> inputMap = new HashMap<>();
////    	inputMap.put("brand", "Adidas");
////    	inputMap.put("article_type", "Shirt");
////    	inputMap.put("style_id", "3");
////    	inputMap.put("is_active", "0");
////    	inputMap.put("rule_output_id", "3");
////    	rs.addRule(inputMap);
//
////    	rs.deleteRule(rule);
//    }
}
