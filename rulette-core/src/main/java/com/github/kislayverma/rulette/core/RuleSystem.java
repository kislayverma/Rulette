package com.github.kislayverma.rulette.core;

import com.github.kislayverma.rulette.core.dao.DataSource;
import com.github.kislayverma.rulette.core.dao.RuleSystemDao;
import com.github.kislayverma.rulette.core.dao.impl.RuleSystemDaoMySqlImpl;
import com.github.kislayverma.rulette.core.evaluationengine.IEvaluationEngine;
import com.github.kislayverma.rulette.core.evaluationengine.impl.trie.TrieBasedEvaluationEngine;
import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaData;
import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaDataFactory;
import com.github.kislayverma.rulette.core.rule.Rule;
import com.github.kislayverma.rulette.core.ruleinput.RuleInputMetaData;
import com.github.kislayverma.rulette.core.validator.DefaultValidator;
import com.github.kislayverma.rulette.core.validator.Validator;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class models a rule-system comprising of rules and provides appropriate
 * APIs to interact with it.
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

    private Validator validator;
    private RuleSystemMetaData metaData;
    private RuleSystemDao dao;
    private IEvaluationEngine evaluationEngine;

    /**
     * This constructor is added only to support unit testing. Should not be used.
     */
    public RuleSystem() {
    }

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
        this(ruleSystemName, validator, null, null);
    }

    public RuleSystem(String ruleSystemName, Validator validator, RuleSystemDao ruleSystemDao, String datasourceUrl) throws Exception {
        // Set up database classes
        datasourceUrl = (datasourceUrl == null || datasourceUrl.equals("")) ? "rulette-datasource.properties" : datasourceUrl;
        DataSource.init(datasourceUrl);
        this.dao = (ruleSystemDao == null) ? new RuleSystemDaoMySqlImpl() : ruleSystemDao;

        this.validator = (validator != null) ? validator : new DefaultValidator();

        long startTime = new Date().getTime();
        initRuleSystem(ruleSystemName);
        long endTime = new Date().getTime();
        System.out.println("Time taken to initialize rule system : " + (endTime - startTime) + " ms.");
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

        long startTime = new Date().getTime();
        initRuleSystem(ruleSystemName);
        long endTime = new Date().getTime();
        System.out.println("Time taken to initialize rule system : " + (endTime - startTime) + " ms.");
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
     * This method returns a list of all the rules in the rule system.
     * @param inputMap
     * @return 
     * @throws java.lang.Exception 
     */
    public List<Rule> getAllApplicableRules(Map<String, String> inputMap) throws Exception {
        return evaluationEngine.getAllApplicableRules(inputMap);
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

            return newRule;
        } else {
            throw new RuntimeException("The following existing rules conflict with "
                    + "the given input : " + overlappingRules);
        }
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
        newRule = newRule.setColumnData(metaData.getUniqueIdColumnName(), oldRuleId);
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
     * 1. Get rule system inputs from rule_system.rule_input table.
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
        File f = new File("/Users/kislay.verma/Applications/apache-tomcat-7.0.53/conf/rulette-datasource.properties");
        RuleSystem rs = new RuleSystem("govt_vat_rule_system", null, null, f.getPath());
//        RuleSystem rs = new RuleSystem("govt_vat_rule_system", null, null, "rulette-datasource.properties");
//        RuleSystem rs = new RuleSystem("govt_vat_rule_system", null, null, null);

        Map<String, String> inputMap = new HashMap<>();
        inputMap.put("article_id", "7");
//        inputMap.put("source_state_code", "HAR");
//        inputMap.put("destination_state_code", "GUJ");
//        inputMap.put("courier_code", "IP");
//        inputMap.put("mrp_threshold", "5");
//        inputMap.put("gender", "Women");
//        inputMap.put("is_active", "1");
//        inputMap.put("valid_date_range", "20130820");

        // Rule rule = null;
        long stime = new Date().getTime();
        for (int i = 0; i < 1; i++) {
            Rule rule = rs.getRule(inputMap);
            System.out.println((rule == null) ? "none" : rule.toString());
//            System.out.println("\n---------------------------\n");
//            List<Rule> rules = rs.getAllApplicableRules(inputMap);
//            System.out.println((rules == null) ? "none" : rules.toString());
        }

        long etime = new Date().getTime();
        System.out.println("Time taken to get rule : " + (etime - stime) + " ms.");
    }
}
