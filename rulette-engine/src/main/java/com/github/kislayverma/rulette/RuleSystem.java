package com.github.kislayverma.rulette;

import com.github.kislayverma.rulette.core.data.IDataProvider;
import com.github.kislayverma.rulette.core.engine.IEvaluationEngine;
import com.github.kislayverma.rulette.core.exception.DataAccessException;
import com.github.kislayverma.rulette.core.exception.RuleConflictException;
import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaData;
import com.github.kislayverma.rulette.core.rule.Rule;
import com.github.kislayverma.rulette.core.ruleinput.RuleInputConfigurator;
import com.github.kislayverma.rulette.core.metadata.RuleInputMetaData;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.github.kislayverma.rulette.core.ruleinput.type.RuleInputType;
import com.github.kislayverma.rulette.core.util.RuletteInputProcessor;
import com.github.kislayverma.rulette.engine.impl.trie.TrieBasedEvaluationEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class models a rule-system comprising of rules and provides appropriate
 * APIs to interact with it.
 * 
 * @author Kislay Verma
 *
 */
public class RuleSystem implements Serializable {
    private static final long serialVersionUID = -4642927907092556335L;

    private RuleSystemMetaData metaData;
    private IDataProvider dataProvider;
    private IEvaluationEngine evaluationEngine;
    private final RuleInputConfigurator inputConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleSystem.class);

    /**
     * Utility method to create a new rule system via the given data provider
     *
     * @param metaData
     * @param dataProvider
     */
    public static void createNewRuleSystem(RuleSystemMetaData metaData, IDataProvider dataProvider) {
        dataProvider.createRuleSystem(metaData);
    }

    /**
     * Utility method to delete a rule system backed by the given data provider
     * @param ruleSystemName
     */
    public static void deleteRuleSystem(String ruleSystemName, IDataProvider dataProvider) {
        dataProvider.deleteRuleSystem(ruleSystemName);
    }

    /**
     * This constructor initializes a rule system of the given name by reading data from the
     * credentials given in the data source URL. All rule input will be initiatized with default parameters
     * and no custom data types will be supported.
     * 
     * @param ruleSystemName Name of the rule system to be instantiated
     * @param dataProvider An {@link IDataProvider} to load rules from

     */
    public RuleSystem(String ruleSystemName, IDataProvider dataProvider) throws RuleConflictException {
        this(ruleSystemName, dataProvider, null);
    }

    /**
     * This constructor initializes a rule system of the given name by reading data from the
     * credentials given in the data source URL. Rule input will be initiatized with default 
     * parameters unless an override is provided via the inputConfig parameter. Custom data 
     * type will be supported only if appropriate configuration is provided.
     * 
     * @param ruleSystemName Name of the rule system to be instantiated
     * @param dataProvider An {@link IDataProvider} to load rules from
     * @param inputConfig Configuration to support custom data types and behaviour for rule inputs
     */
    public RuleSystem(String ruleSystemName, IDataProvider dataProvider, RuleInputConfigurator inputConfig)
        throws RuleConflictException {
        long startTime = new Date().getTime();
        this.dataProvider = dataProvider;
        this.inputConfig = inputConfig;

        initRuleSystem(ruleSystemName, dataProvider, inputConfig);
        long endTime = new Date().getTime();
        LOGGER.info("Time taken to initialize rule system : " + (endTime - startTime) + " ms.");
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
     */
    public List<Rule> getAllApplicableRules(Map<String, String> inputMap) {
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
     * @throws java.lang.IllegalAccessException if rule inputs cannot be accessed on the object
     */
    public Rule getRule(Object request) throws IllegalAccessException {
        Map<String, String> inputMap = RuletteInputProcessor.generateInputMap(request);
        return evaluationEngine.getRule(inputMap);
    }

    /**
     * This method returns the rule applicable for the given combination of rule
     * inputs.
     *
     * @param inputMap Map with input names as keys and their String values as
     * values
     * @return null if input is null, null if no rule is applicable for the
     * given input combination the applicable rule otherwise.
     */
    public Rule getRule(Map<String, String> inputMap) {
        return evaluationEngine.getRule(inputMap);
    }

    /**
     * This method returns the applicable rule for the given input criteria.
     *
     * @param ruleId Unique id of the rule to get looked up.
     * @return A {@link Rule} object if a rule with the given id exists. null
     * otherwise.
     */
    public Rule getRule(String ruleId) {
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
     * @throws RuleConflictException if the rule to be added is a conflicting rule
     */
    public Rule addRule(Map<String, String> inputMap) throws RuleConflictException {
        if (inputMap == null) {
            return null;
        }

        Rule newRule = new Rule(metaData, inputMap);
        return addRule(newRule);
    }

    /**
     * This method adds the given rule to the rule system with a new rule id.
     *
     * @param newRule The new rule to be added
     * @return the added rule if there are no overlapping rules null if there
     * are overlapping rules null if the input constitutes an invalid rule as
     * per the validation policy in use.
     * @throws RuleConflictException if the rule to be added is a conflicting rule
     */
    public Rule addRule(Rule newRule) throws RuleConflictException {
        if (newRule == null) {
            return null;
        }

        // Rule output must be some proper value, it can not be 'Any'
        if (newRule.getColumnData(metaData.getUniqueOutputColumnName()).isAny()) {
            throw new RuntimeException("Rule can't be saved without rule_output_id.");
        }

        List<Rule> overlappingRules = getConflictingRules(newRule);
        if (overlappingRules.isEmpty()) {
            newRule = dataProvider.saveRule(metaData.getRuleSystemName(), newRule);
            if (newRule != null) {
                evaluationEngine.addRule(newRule);
            }

            return newRule;
        } else {
            throw new RuleConflictException("The following existing rules conflict with "
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
     * @throws RuleConflictException if the updated rule is a conflicting rule
     */
    public Rule updateRule(Rule oldRule, Rule newRule) throws RuleConflictException {
        if (oldRule == null || newRule == null) {
            return null;
        }

        String oldRuleId = oldRule.getId();
        Rule checkForOldRule = this.getRule(oldRuleId);
        if (checkForOldRule == null) {
            throw new IllegalArgumentException("No existing rule with id " + oldRuleId);
        }

        List<Rule> overlappingRules = getConflictingRules(newRule);
        if (!overlappingRules.isEmpty()) {
            boolean otherOverlappingRules = false;
            for (Rule overlappingRule : overlappingRules) {
                if (!overlappingRule.getId().equals(oldRuleId)) {
                    otherOverlappingRules = true;
                }
            }

            if (otherOverlappingRules) {
                throw new RuleConflictException("The following existing rules conflict with "
                        + "the given input : " + overlappingRules);
            }
        }
        Rule newRuleWithOldIdSet = newRule.setColumnData(metaData.getUniqueIdColumnName(), oldRuleId);
        Rule resultantRule = dataProvider.updateRule(metaData.getRuleSystemName(), newRuleWithOldIdSet);
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
     */
    public boolean deleteRule(String ruleId) {
        if (ruleId != null) {
            Rule rule = getRule(ruleId);
            return deleteRule(rule);
        }

        return false;
    }

    /**
     * This method deletes the given rule from the rule system.
     *
     * @param rule The {@link Rule} to be deleted.
     * @return true if the given rule was successfully deleted false if the
     * given rule does not exist false if the given rule could not be deleted
     * (for whatever reason).
     */
    public boolean deleteRule(Rule rule) {
        if (rule == null) {
            return false;
        }

        boolean status = dataProvider.deleteRule(metaData.getRuleSystemName(), rule);
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
     */
    public List<Rule> getConflictingRules(Rule rule) {
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
     */
    public Rule getNextApplicableRule(Map<String, String> inputMap) {
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

    /**
     * this method returns the metadata corresponding to this rule system. A copy of the actual data is returned to
     * preserve immutability of the original data.
     * @return {@link RuleSystemMetaData} for this rule system
     */
    public RuleSystemMetaData getMetaData() {
        return new RuleSystemMetaData(
            this.metaData.getRuleSystemName(),
            this.metaData.getTableName(),
            this.metaData.getUniqueIdColumnName(),
            this.metaData.getUniqueOutputColumnName(),
            this.metaData.getInputColumnList());
    }

    /**
     * This method adds a new rule input to this rule system. The current rule system metadata and rules are not
     * impacted and the reload() API must be called explicitly by the user to bring the new rule input into effect.
     *
     * @param ruleInput  The rule input definition to be added
     */
    public void addRuleInput(RuleInputMetaData ruleInput) {
        if (ruleInput.getName() == null || ruleInput.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Undefined name for new rule input");
        }
        if (ruleInput.getName().equals(this.metaData.getUniqueIdColumnName()) ||
            ruleInput.getName().equals(this.metaData.getUniqueOutputColumnName())) {
            throw new IllegalArgumentException("A rule input with the same name already exists");
        }
        for (RuleInputMetaData inputColumn : this.metaData.getInputColumnList()) {
            if (ruleInput.getName().equals(inputColumn.getName())) {
                throw new IllegalArgumentException("A rule input with the same name already exists");
            }
        }
        if (ruleInput.getRuleInputType() == null) {
            throw new IllegalArgumentException("Undefined rule input type for new rule input");
        }
        if (ruleInput.getRuleInputType() == RuleInputType.RANGE &&
            (ruleInput.getRangeLowerBoundFieldName() == null
                || ruleInput.getRangeLowerBoundFieldName().trim().isEmpty()
                || ruleInput.getRangeUpperBoundFieldName() == null
                || ruleInput.getRangeUpperBoundFieldName().trim().isEmpty())) {
            throw new IllegalArgumentException("New range input does not have upper and lower column names defined");
        }
        if (ruleInput.getDataType() == null || ruleInput.getDataType().trim().isEmpty()) {
            throw new IllegalArgumentException("Undefined data type for new rule input");
        }
        for (RuleInputMetaData inputColumn : this.metaData.getInputColumnList()) {
            if (ruleInput.getPriority() == inputColumn.getPriority()) {
                throw new IllegalArgumentException("New rule input has the same priority as " + ruleInput.getName());
            }
        }

        this.dataProvider.addRuleInput(this.metaData.getRuleSystemName(), ruleInput);
    }

    /**
     * This method deletes the rule input of the given name from this rule system. The loaded rule system metadata and
     * rules are not impacted and the reload() API must be called explicitly by the user to bring the new rule input
     * into effect.
     *
     * @param ruleInputName The rule input definition to be deleted
     */
    public void deleteRuleInput(String ruleInputName) {
        if (ruleInputName == null || ruleInputName.trim().isEmpty()) {
            throw new IllegalArgumentException("Undefined rule input name for deletion");
        }
        if (ruleInputName.equals(this.metaData.getUniqueIdColumnName()) ||
            ruleInputName.equals(this.metaData.getUniqueOutputColumnName())) {
            throw new IllegalArgumentException("Input and output columns cannot be deleted");
        }
        boolean columnFound = false;
        for (RuleInputMetaData inputColumn : this.metaData.getInputColumnList()) {
            if (ruleInputName.equals(inputColumn.getName())) {
                columnFound = true;
            }
        }
        if (!columnFound) {
            throw new IllegalArgumentException("No input named " + ruleInputName + " found to delete");
        }

        this.dataProvider.deleteRuleInput(this.metaData.getRuleSystemName(), ruleInputName);
    }

    /**
     * This method reloads the whole rule system by pulling fresh configuration and data from the data provider
     * @throws RuleConflictException
     */
    public synchronized void reload() throws RuleConflictException {
        initRuleSystem(this.metaData.getRuleSystemName(), this.dataProvider, this.inputConfig);
    }

    /*
     * 1. Get rule system inputs from rule_system.rule_input table.
     * 2. Get rules from the table specified for this rule system in the
     *    rule_system..rule_system table
     */
    private void initRuleSystem(String ruleSystemName, IDataProvider dataProvider, RuleInputConfigurator inputConfig)
        throws DataAccessException, RuleConflictException {
        this.metaData = dataProvider.getRuleSystemMetaData(ruleSystemName);
        this.metaData.applyCustomConfiguration(inputConfig);

        LOGGER.info("Loading rules from store...");
        List<Rule> rules = dataProvider.getAllRules(ruleSystemName);
        LOGGER.info(rules.size() + " rules loaded");

        this.evaluationEngine = new TrieBasedEvaluationEngine(metaData);

        for (Rule rule : rules) {
            evaluationEngine.addRule(rule);
        }
    }
}
