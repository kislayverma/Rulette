package com.github.kislayverma.rulette.core;

import com.github.kislayverma.rulette.core.dao.DataSource;
import com.github.kislayverma.rulette.core.dao.RuleSystemDao;
import com.github.kislayverma.rulette.core.dao.impl.mysql.RuleSystemDaoImpl;
import com.github.kislayverma.rulette.core.engine.IEvaluationEngine;
import com.github.kislayverma.rulette.core.engine.impl.trie.TrieBasedEvaluationEngine;
import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaData;
import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaDataFactory;
import com.github.kislayverma.rulette.core.rule.Rule;
import com.github.kislayverma.rulette.core.ruleinput.RuleInputConfigurator;
import com.github.kislayverma.rulette.core.metadata.RuleInputMetaData;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private RuleSystemMetaData metaData;
    private RuleSystemDao dao;
    private IEvaluationEngine evaluationEngine;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleSystem.class);

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
     * @param ruleSystemName name of the rule system to initialize
     * @throws Exception on rule system initialization failure
     */
    public RuleSystem(String ruleSystemName) throws Exception {
        this(ruleSystemName, null);
    }

    /**
     * This constructor initializes a rule system of the given name by reading data from the
     * credentials given in the data source URL. All rule input will be initiatized with default parameters
     * and no custom data types will be supported.
     * 
     * @param ruleSystemName Name of the rule system to be instantiated
     * @param datasourceUrl Path to a properties file containing data source configuration
     * @throws Exception on rule system initialization failure
     */
    public RuleSystem(String ruleSystemName, String datasourceUrl) throws Exception {
        this(ruleSystemName, datasourceUrl, null);
    }

    /**
     * This constructor initializes a rule system of the given name by reading data from the
     * credentials given in the data source URL. Rule input will be initiatized with default 
     * parameters unless an override is provided via the inputConfig parameter. Custom data 
     * type will be supported only if appropriate configuration is provided.
     * 
     * @param ruleSystemName Name of the rule system to be instantiated
     * @param datasourceUrl Path to a properties file containing data source configuration
     * @param inputConfig Configuration to support custom data types and behaviour for rule inputs
     * @throws Exception if rule system could not be initialized
     */
    public RuleSystem(String ruleSystemName, String datasourceUrl, RuleInputConfigurator inputConfig) throws Exception {
        // Set up database classes
        long startTime = new Date().getTime();
        datasourceUrl = (datasourceUrl == null || datasourceUrl.equals("")) ? "rulette-datasource.properties" : datasourceUrl;
        DataSource.init(datasourceUrl);
        this.dao = new RuleSystemDaoImpl();

        initRuleSystem(ruleSystemName, inputConfig);
        long endTime = new Date().getTime();
        LOGGER.info("Time taken to initialize rule system : " + (endTime - startTime) + " ms.");
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
     * @return List of all rules configured in the rule system
     */
    public List<Rule> getAllRules() {
        return evaluationEngine.getAllRules();
    }

    /**
     * This method returns a list of all the rules in the rule system.
     * @param inputMap map of rule input values for which applicable rules are to be returned
     * @return List of all rules applicable to the given input
     * @throws java.lang.Exception if rule evaluation fails
     */
    public List<Rule> getAllApplicableRules(Map<String, String> inputMap) throws Exception {
        return evaluationEngine.getAllApplicableRules(inputMap);
    }

    /**
     * This method returns the rule applicable for the given combination of rule
     * inputs.
     *
     * @param request A simple Object with @RuletteInput annotation on required fields
     * values
     * @return null if input is null, null if no rule is applicable for the
     * given input combination the applicable rule otherwise.
     * @throws java.lang.Exception on rule evaluation error
     */
    public Rule getRule(Object request) throws Exception {
        return evaluationEngine.getRule(request);
    }

    /**
     * This method returns the rule applicable for the given combination of rule
     * inputs.
     *
     * @param inputMap Map with input names as keys and their String values as
     * values
     * @return null if input is null, null if no rule is applicable for the
     * given input combination the applicable rule otherwise.
     * @throws java.lang.Exception on rule evaluation error
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
     * @param inputMap The rule input values for which a new rule is to be added
     * @return the added rule if there are no overlapping rules null if there
     * are overlapping rules null if the input constitutes an invalid rule as
     * per the validation policy in use.
     * @throws Exception on failure
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
     * @param newRule The new rule to be added
     * @return the added rule if there are no overlapping rules null if there
     * are overlapping rules null if the input constitutes an invalid rule as
     * per the validation policy in use.
     * @throws Exception on failure
     */
    public Rule addRule(Rule newRule) throws Exception {
        if (newRule == null) {
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
        if (oldRule == null || newRule == null) {
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
     * @throws Exception on error in deleting rule
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
     * @throws Exception on failure
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
     * @throws Exception on rule evaluation failure
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
     * @throws java.lang.Exception on rule evaluation failure
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

    /**
     * Returns the name of the rule system
     * @return the name of the rule system
     */
    public String getName() {
        return metaData.getRuleSystemName();
    }

    /**
     * Returns the names of all the columns in the rule system, including the unique 
     * input and output column names
     * @return names of all the columns in the rule system
     */
    public List<String> getAllColumnNames() {
        List<String> columnNames = new ArrayList<>();
        columnNames.add(metaData.getUniqueIdColumnName());
        for (RuleInputMetaData rimd : metaData.getInputColumnList()) {
            columnNames.add(rimd.getName());
        }
        columnNames.add(metaData.getUniqueOutputColumnName());

        return columnNames;
    }

    /**
     * Returns the names of all the columns in the rule system, excluding the unique 
     * input and output column names
     * @return names of all columns for evaluation in the rule system
     */
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
    private void initRuleSystem(String ruleSystemName, RuleInputConfigurator inputConfig) throws Exception {
        this.metaData = RuleSystemMetaDataFactory.getInstance().getMetaData(ruleSystemName);
        this.metaData.applyCustomConfiguration(inputConfig);

        LOGGER.info("Loading rules from DB...");
        List<Rule> rules = dao.getAllRules(ruleSystemName);
        LOGGER.info(rules.size() + " rules loaded");

        this.evaluationEngine = new TrieBasedEvaluationEngine(metaData);

        for (Rule rule : rules) {
            evaluationEngine.addRule(rule);
        }
    }

    public static void main(String[] args) throws Exception {
        File f = new File("/Users/kislay.verma/Applications/apache-tomcat-7.0.53/conf/rulette-datasource.properties");
        RuleSystem rs = new RuleSystem("govt_vat_rule_system", f.getPath());

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
