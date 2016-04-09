package com.kislay.rulette.evaluationengine;

import com.kislay.rulette.rule.Rule;
import java.util.List;
import java.util.Map;

/**
 *
 * @author kislay.verma
 */
public interface IEvaluationEngine {

    /**
     * This method returns a list of all the rules in the rule system.
     * @return 
     */
    List<Rule> getAllRules();

    /**
     * This method returns the applicable rule for the given input criteria.
     *
     * @param ruleId Unique id of the rule to get looked up.
     * @return A {@link Rule} object if a rule with the given id exists. null
     * otherwise.
     */
    Rule getRule(Integer ruleId);

    /**
     * This method returns the rule applicable for the given combination of rule
     * inputs.
     *
     * @param inputMap Map with input names as keys and their String values as
     * values
     * @return null if input is null, null if no rule is applicable for the
     * given input combination the applicable rule otherwise.
     * 
     * @throws java.lang.Exception
     */
    Rule getRule(Map<String, String> inputMap) throws Exception;

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
     * 
     * @throws java.lang.Exception
     */
    Rule getNextApplicableRule(Map<String, String> inputMap) throws Exception;

    void addRule(Rule rule) throws Exception;

    void deleteRule(Rule rule) throws Exception;
}
