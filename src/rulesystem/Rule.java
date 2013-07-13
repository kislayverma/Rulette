package rulesystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class models a rule in the rule system. It has input columns and an output value
 * which the rule system maps these inputs to.
 * 
 * @author Kislay
 *
 */
public class Rule {
	private Map<String, String> fieldMap;

	// This list is to keep the order (priority order) of inputs
	private List<String> inputColumnList;

	/**
	 * This package scoped constructor takes the list of columns in the rule system and a map
	 * of value to populate the fields of this rule. Any fields missing in the input are set to 
	 * blank (meaning 'Any').
	 * 
	 * @param colNames
	 * @param inputMap
	 */
	Rule(List<String> colNames, Map<String, String> inputMap) {
		this.inputColumnList = colNames;
		fieldMap = new HashMap<String, String>();
		for (String colName: colNames) {
			String inputVal = inputMap.get(colName);
			this.fieldMap.put(colName, ((inputVal == null) ? "" : inputVal ));
		}
	}

	private Rule() {}

	/**
	 * This method accepts a column name to column value mapping and return if the mapping is 
	 * true for this rule. i.e. It returns true if this rule is applicable for the input values
	 * and false otherwise.
	 * 
	 * The method returns true if one the following criteria are met for each column:
	 * 1. Both rule and input are equal (same value or both being 'Any')
	 * 2. Rule is any.
	 * 
	 * In all other cases false is returned.
	 * 
	 * @param inputMap
	 * @return
	 */
	public boolean evaluate(Map<String, String> inputMap) {
    	// For each input column in order, get the value from the rule and compare against input.
    	for (String colName : this.inputColumnList) {
    		if (colName.equals(RuleSystem.UNIQUE_ID_COLUMN_NAME) ||
    			colName.equals(RuleSystem.UNIQUE_OUTPUT_COLUMN_NAME))
    		{
    			continue;
    		}

    		String inputValue = inputMap.get(colName);
    		if (inputValue == null) {
        		System.out.println("Input doesn't contain field '" + colName + "'. Assuming 'Any'.");
        		inputValue = "";
    		}

    		String ruleValue = this.getValueForColumn(colName);
    		if (ruleValue == null) {
        		System.out.println("Rule doesn't contain field '" + colName + "'. Assuming 'Any'.");
        		ruleValue = "";
    		}
 
    		// Actual comparison handling 'Any' cases.
    		if (ruleValue.isEmpty() || ruleValue.equals(inputValue)) {
    			continue;
    		}
    		else {
    			return false;
    		}
    	}

    	return true;
	}

	public String getValueForColumn(String colName) {
		return this.fieldMap.get(colName);
	}
}
