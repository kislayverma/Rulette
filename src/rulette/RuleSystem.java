package rulette;

import rulette.rule.Rule;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rulette.dao.RuleSystemDao;
import rulette.dao.impl.RuleSystemDaoMySqlImpl;
import rulette.evaluationengine.IEvaluationEngine;
import rulette.evaluationengine.impl.trie.TrieBasedEvaluationEngine;
import rulette.metadata.RuleSystemMetaData;
import rulette.metadata.RuleSystemMetaDataFactory;
import rulette.ruleinput.RuleInputMetaData;
import rulette.validator.DefaultValidator;
import rulette.validator.Validator;

/**
 * This class models a rule-system comprising of rules and provides appropriate
 * APIs to interact with it.
 *
 * A rule-system, in this context, is a mapping of elements of an input space
 * comprising of one or more distinct inputs to a well-defined output space.
 * This is a generic implementation which allows for creation and management of
 * these mappings. Much can be read about rule-systems elsewhere (Drools is a
 * particularly well known and elaborate implementation), so I will just lay out
 * the specifics of this particular implementation:
 *<ol>
 * <li>This is a lightweight, easy to setup implementation, agnostic to the input
 * and output domains. The offered APIs deal only with mappings (henceforth
 * called rules) and take no cognizance of what the inputs and output mean. This
 * is by design. To use this in an application, I would expect that you would
 * wrap this core engine with a module which understands the semantics of your
 * application.</li>
 * <li>An example of a rule would be If X= 2 AND Y = 3, THEN Z =42.
 * To match any value of an input, just pass null, like so : If X= null AND Y =
 * 3, THEN Z = 51 
 * </li>
 * <li>A 'rule input' is a criterion, which in combination with
 * other of its kind, decides an outcome. In #2, X and Y are rule inputs.</li>
 * <li>2 types of rule input are supported : 'Value' and 'Range'. Value inputs are
 * discrete valued criteria, while range inputs define ranges in the input
 * space.</li>
 * <li>Only 'AND' operation between the rule inputs is supported.</li>
 * <li>All rule inputs are treated as strings. The ranges defined by range inputs are
 * also interpreted as string ranges. This might require you to invest some
 * thought into how you want to model your rules. e.g. To have a date range an
 * an input, then a possible way to specify it as CCYYMMDD representations of
 * the start and end dates. This defines a range just as well as actual dates.</li>
 * <li>Input have a priority order. This is the order in which they are evaluated
 * to arrive at the output. Defining priorities is much like defining database
 * indexes - different choices can cause widely divergent performance.
 * Worse-depending on your domain, incorrect priorities may even lead to
 * incorrect results.</li>
 * <li>Rules are captured in database tables (one per rule
 * system). These tables must have two columns : 'rule_id' (unique id for the
 * rule, preferable an auto-incrementing primary key) and 'rule_output_id'
 * (unique identifier for the output). The engine doesn't care what you do with
 * the rule_output_id. It is simply what the inputs map to. It may be a foreign
 * key reference to another table . It may be the actual value you need. It
 * simply doesn't matter to this system. The other columns each represent an
 * input.</li>
 * <li>To do rule evaluation, the system takes the combination of the
 * different rule inputs given to it, and returns the best fitting rule (if
 * any). 'Best fit' means: a. Value inputs - An exact value match is better than
 * an 'any' match. e.g. if there are two rules, one with value of input X as 1
 * and the other as any, then on passing X = 1, the former rule will be
 * returned. On passing X = 2, the latter will be returned (as the former
 * obviously doesn't match). b. Range inputs : A tighter range is a better fit
 * than a wider range. e.g. if there are two rules, one with value of input X as
 * Jan 1 2013 to Dec31, 2013 and the other as Feb 1 2013 to March 1 2013, then
 * on passing X = Feb 15, 2013, the latter will be returned.</li>
 * <li>Conflicting rules are those that will, if present in the system, cause ambiguity at the
 * time of rule evaluation. The addRule APIs provided do not allow addition of
 * conflicting rules.</li>
 *</ol>
 *
 * <b>Pre-requisites</b>
 * <ol>
 * <li>Java 1.7 2</li>
 * <li>MySQL 5.x (Support for other databases will be added on demand)</li>
 * </ol>
 *
 * <b>How to setup</b>
 * <ol>
 * <li>Execute the setup.sql script on your MySQL server. This creates a database called rule_system and creates the necessary
 * table in it.</li>
 * <li>Create a table containing your rules as defined in #7 above</li>
 * <li>Map this table in the rule_system.rule_system table as shown in the sample-0setup.sql script.</li>
 * <li>For each rule input, add a row to the rule_system.rule_input table with the
 * input's type (Value/Range) and priority order.</li>
 * <li>5. Put the jar in your class path.</li>
 *</ol>
 * That's it! The rule system is all set up and ready to use.
 *
 * 
 * <b>Sample usage</b>
 * <pre>
 * <code>
 * RuleSystem rs = new RuleSystem("rule system name");
 * Rule r = rs.getRule(25L);
 * </code>
 * </pre>
 *
 * @author Kislay Verma
 *
 */
public class RuleSystem implements Serializable {

    private final Validator validator;
    private RuleSystemMetaData metaData;
    private RuleSystemDao dao;
    private IEvaluationEngine evaluationEngine;

    /**
     * This constructor accepts a path to a text file containing the following
     * values on separate lines: 1. Name of the rule system 2. Full path of the
     * file containing the rules
     *
     * @param ruleSystemName
     * @param validator
     * @throws Exception
     */
    public RuleSystem(String ruleSystemName, Validator validator) throws Exception {
        this(ruleSystemName,
             validator,
             new RuleSystemDaoMySqlImpl());
    }

    /**
     * Use this constructor to explicitly set the dao to be used by the Rule System.
     * This is optional as the rule has a default implementation of all database
     * operations.
     *
     * Inserting your custom dao is a big responsibility that must not be taken
     * up lightly. You can ,however, use this facility in case you must work
     * with pre-existing database systems or to integrate with frameworks like
     * Hibernate.
     * 
     * @param ruleSystemName
     * @param validator
     * @param ruleSystemDao
     * @throws java.lang.Exception
     */
    public RuleSystem(String ruleSystemName, Validator validator, RuleSystemDao ruleSystemDao) throws Exception {
        this.validator = (validator != null) ? validator : new DefaultValidator();
        this.dao = ruleSystemDao;

        initRuleSystem(ruleSystemName);
    }

    public Rule createRuleObject(Map<String, String> inputMap) throws Exception {
        if (inputMap == null) {
            throw new Exception("No input for creating rule object");
        }
        if (!inputMap.containsKey(metaData.getUniqueOutputColumnName())) {
            throw new Exception("Value for rule output not provided");
        }

        return new Rule(metaData.getRuleSystemName(), inputMap);
    }

    /**
     * This method returns a list of all the rules in the rule system.
     * @return 
     */
    public List<Rule> getAllRules() {
        return evaluationEngine.getAllRules();
    }

    /**
     * This method returns the rule applicable for the given combination of rule
     * inputs.
     *
     * @param inputMap Map with input names as keys and their String values as
     * values
     * @return null if input is null, null if no rule is applicable for the
     * given input combination the applicable rule otherwise.
     * @throws java.lang.Exception
     */
    public Rule getRule(Map<String, String> inputMap) throws Exception {
        return evaluationEngine.getRule(inputMap);
    }

    /**
     * This method returns the applicable rule for the given input criteria.
     *
     * @param ruleId Unique id of the rule to get looked up.
     * @return A {@link Rule} object if a rule with the given id exists. null
     * otherwise.
     */
    public Rule getRule(Integer ruleId) {
        return evaluationEngine.getRule(ruleId);
    }

    /**
     * This method adds a new rule to the rule system. There is no need to
     * provide the rule_id field in the input - it will be auto-populated.
     *
     * @param inputMap
     * @return the added rule if there are no overlapping rules null if there
     * are overlapping rules null if the input constitutes an invalid rule as
     * per the validation policy in use.
     * @throws Exception
     */
    public Rule addRule(Map<String, String> inputMap) throws Exception {
        if (inputMap == null) {
            return null;
        }

        Rule newRule = new Rule(metaData.getRuleSystemName(), inputMap);
        return addRule(newRule);
    }

    /**
     * This method adds the given rule to the rule system with a new rule id.
     *
     * @param newRule
     * @return the added rule if there are no overlapping rules null if there
     * are overlapping rules null if the input constitutes an invalid rule as
     * per the validation policy in use.
     * @throws Exception
     */
    public Rule addRule(Rule newRule) throws Exception {
        if (newRule == null || !this.validator.isValid(newRule)) {
            return null;
        }

        String ruleOutputId = newRule.getColumnData(metaData.getUniqueOutputColumnName()).getRawValue();
        if (ruleOutputId == null || ruleOutputId.isEmpty()) {
            throw new RuntimeException("Rule can't be saved without rule_output_id.");
        }

        List<Rule> overlappingRules = getConflictingRules(newRule);
        if (overlappingRules.isEmpty()) {
            newRule = dao.saveRule(metaData.getRuleSystemName(), newRule);
            if (newRule != null) {
                evaluationEngine.addRule(newRule);
            }
        } else {
            throw new RuntimeException("The following existing rules conflict with "
                    + "the given input : " + overlappingRules);
        }
        throw new RuntimeException("Faild to save rule. Check logs for errors");
    }

    /**
     * This method updates an existing rules with values of the new rule given.
     * All fields are updated of the old rule are updated. The new rule is
     * checked for conflicts before update.
     *
     * @param oldRule An existing rule
     * @param newRule The rule containing the new field values to which the old
     * rule will be updated.
     * @return the updated rule if update creates no conflict. null if the input
     * constitutes an invalid rule as per the validation policy in use.
     * @throws Exception if there are overlapping rules if the old rules does
     * not actually exist.
     */
    public Rule updateRule(Rule oldRule, Rule newRule) throws Exception {
        if (oldRule == null || newRule == null || !this.validator.isValid(newRule)) {
            return null;
        }

        String oldRuleId = oldRule.getColumnData(metaData.getUniqueIdColumnName()).getRawValue();
        Rule checkForOldRule = this.getRule(Integer.parseInt(oldRuleId));
        if (checkForOldRule == null) {
            throw new Exception("No existing rule with id " + oldRuleId);
        }

        List<Rule> overlappingRules = getConflictingRules(newRule);
        if (!overlappingRules.isEmpty()) {
            boolean otherOverlappingRules = false;
            for (Rule overlappingRule : overlappingRules) {
                if (!overlappingRule.getColumnData(metaData.getUniqueIdColumnName()).getRawValue()
                        .equals(oldRuleId)) {
                    otherOverlappingRules = true;
                }
            }

            if (otherOverlappingRules) {
                throw new RuntimeException("The following existing rules conflict with "
                        + "the given input : " + overlappingRules);
            }
        }

        Rule resultantRule = dao.updateRule(metaData.getRuleSystemName(), newRule);
        if (resultantRule != null) {
            evaluationEngine.deleteRule(oldRule);
            evaluationEngine.addRule(resultantRule);
        }

        return resultantRule;
    }

    /**
     * This method deletes an existing rule from the rule system.
     *
     * @param ruleId Unique id of the rule to be deleted
     * @return true if the rule with given rule id was successfully deleted
     * false if the given rule does not exist false if the given rule could not
     * be deleted (for whatever reason).
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
     * @return true if the given rule was successfully deleted false if the
     * given rule does not exist false if the given rule could not be deleted
     * (for whatever reason).
     * @throws Exception
     */
    public boolean deleteRule(Rule rule) throws Exception {
        if (rule == null) {
            return false;
        }

        boolean status = dao.deleteRule(metaData.getRuleSystemName(), rule);
        if (status) {
            evaluationEngine.deleteRule(rule);
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
        List<Rule> conflictingRules = new ArrayList<>();

        for (Rule r : evaluationEngine.getAllRules()) {
            if (r.isConflicting(rule)) {
                conflictingRules.add(r);
            }
        }

        return conflictingRules;
    }

    /**
     * This method returns the next rule that will be applicable to the inputs
     * if the current rule applicable to the were to be deleted.
     *
     * @param inputMap Map with column Names as keys and column values as
     * values.
     * @return A {@link Rule} object if a rule is applicable after the currently
     * applicable rule is deleted. null if no rule is applicable after the
     * currently applicable rule is deleted. null id no rule is currently
     * applicable.
     * @throws java.lang.Exception
     */
    public Rule getNextApplicableRule(Map<String, String> inputMap) throws Exception {
        return evaluationEngine.getNextApplicableRule(inputMap);
    }

    public String getUniqueColumnName() {
        return metaData.getUniqueIdColumnName();
    }

    public String getOutputColumnName() {
        return metaData.getUniqueOutputColumnName();
    }

    public String getName() {
        return metaData.getRuleSystemName();
    }

    public List<String> getAllColumnNames() {
        List<String> columnNames = new ArrayList<>();
        columnNames.add(metaData.getUniqueIdColumnName());
        for (RuleInputMetaData rimd : metaData.getInputColumnList()) {
            columnNames.add(rimd.getName());
        }
        columnNames.add(metaData.getUniqueOutputColumnName());

        return columnNames;
    }

    public List<String> getInputColumnNames() {
        List<String> columnNames = new ArrayList<>();
        for (RuleInputMetaData rimd : metaData.getInputColumnList()) {
            columnNames.add(rimd.getName());
        }

        return columnNames;
    }

    /*
     * 1. Get rule system inputs from rule_system..rule_input table.
     * 2. Get rules from the table specified for this rule system in the
     *    rule_system..rule_system table
     */
    private void initRuleSystem(String ruleSystemName) throws Exception {
        this.metaData = RuleSystemMetaDataFactory.getInstance().getMetaData(ruleSystemName);

        System.out.println("Loading rules from DB...");
        List<Rule> rules = dao.getAllRules(ruleSystemName);
        System.out.println(rules.size() + " rules loaded");

        this.evaluationEngine = new TrieBasedEvaluationEngine(metaData);

        for (Rule rule : rules) {
            if (this.validator.isValid(rule)) {
                evaluationEngine.addRule(rule);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        long stime = new Date().getTime();
        RuleSystem rs = new RuleSystem("vendor_terms_rule_system", null);
        long etime = new Date().getTime();
        System.out.println("Time taken to init rule system : " + (etime - stime));

        Map<String, String> inputMap = new HashMap<>();
        inputMap.put("vendor_name", "SIA FASHION");
        inputMap.put("brand_name", "SIA Fashion");
        inputMap.put("article_type_name", "Kurtas");
        inputMap.put("gender", "omen");
        inputMap.put("is_active", "1");
        inputMap.put("valid_date_range", "20140101");
        Rule rule = null;
        stime = new Date().getTime();
        for (int i = 0; i < 1; i++) {
            rule = rs.getRule(inputMap);
            //System.out.println((rule == null) ? "none" : rule.toString());
        }
        etime = new Date().getTime();
        System.out.println("Time taken : " + (etime - stime));
    }
}
