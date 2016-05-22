package com.github.kislayverma.rulette.core.rule;

import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaData;
import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaDataFactory;
import com.github.kislayverma.rulette.core.ruleinput.RuleInput;
import com.github.kislayverma.rulette.core.ruleinput.RuleInputMetaData;
import com.github.kislayverma.rulette.core.ruleinput.RuleInputType;
import com.github.kislayverma.rulette.core.ruleinput.value.RuleInputDataType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class models a rule in the rule system. It has input columns and an
 * output value which the rule system maps these inputs to.
 *
 * @author Kislay
 *
 */
public class Rule implements Serializable {

    private final String ruleSystemName;
    private final Map<String, RuleInput> fieldMap;
    private final int UNIQUE_ID_INPUT_ID = -1;
    private final int UNIQUE_OUTPUT_ID_INPUT_ID = -2;

    /**
     * This constructor takes the list of columns in the rule system and a map
     * of value to populate the fields of this rule. Any fields missing in the
     * input are set to blank (meaning 'Any').
     *
     * @param ruleSystemName
     * @param inputMap
     *
     * @throws Exception
     */
    public Rule(String ruleSystemName, Map<String, String> inputMap) throws Exception {
        RuleSystemMetaData ruleSystemMetaData =
            RuleSystemMetaDataFactory.getInstance().getMetaData(ruleSystemName);

        this.ruleSystemName = ruleSystemName;
        this.fieldMap = new HashMap<>();

        // Construct all rule inputs
        for (RuleInputMetaData col : ruleSystemMetaData.getInputColumnList()) {
            String inputVal = inputMap.get(col.getName());
            this.fieldMap.put(col.getName(),
                RuleInput.createRuleInput(col.getId(),
                col.getName(),
                col.getPriority(),
                col.getRuleInputType(),
                col.getRuleInputDataType(),
                (inputVal == null) ? "" : inputVal));
        }

        // Construct rule input object representing unique id
        String ruleId = inputMap.get(ruleSystemMetaData.getUniqueIdColumnName());
        this.fieldMap.put(ruleSystemMetaData.getUniqueIdColumnName(),
            RuleInput.createRuleInput(UNIQUE_ID_INPUT_ID,
            ruleSystemMetaData.getUniqueIdColumnName(),
            UNIQUE_ID_INPUT_ID,
            RuleInputType.VALUE,
            RuleInputDataType.NUMBER,
            (ruleId == null) ? "" : ruleId));

        // Construct rule input object representing ouput column
        String ruleOutputId = inputMap.get(ruleSystemMetaData.getUniqueOutputColumnName());
        this.fieldMap.put(ruleSystemMetaData.getUniqueOutputColumnName(),
            RuleInput.createRuleInput(UNIQUE_OUTPUT_ID_INPUT_ID,
            ruleSystemMetaData.getUniqueOutputColumnName(),
            UNIQUE_OUTPUT_ID_INPUT_ID,
            RuleInputType.VALUE,
            RuleInputDataType.NUMBER,
            (ruleOutputId == null) ? "" : ruleOutputId));
    }

    /**
     * This method accepts a column name to column value mapping and return if
     * the mapping is true for this rule. i.e. It returns true if this rule is
     * applicable for the input values and false otherwise.
     *
     * The method returns true if one the following criteria are met for each
     * column:
     * <ol>
     * <li>Both rule and input are equal (same value or both being 'Any')</li>
     * <li>Rule is "any"</li>
     * </ol>
     * In all other cases false is returned.
     *
     * @param inputMap
     * @return
     * @throws java.lang.Exception
     */
    public boolean evaluate(Map<String, String> inputMap) throws Exception {
        RuleSystemMetaData ruleSystemMetaData =
            RuleSystemMetaDataFactory.getInstance().getMetaData(this.ruleSystemName);

        // For each input column in order, get the value from the rule and compare against input.
        for (RuleInputMetaData col : ruleSystemMetaData.getInputColumnList()) {
            String colName = col.getName();

            if (colName.equals(ruleSystemMetaData.getUniqueIdColumnName())
                    || colName.equals(ruleSystemMetaData.getUniqueOutputColumnName())) {
                continue;
            }

            String inputValue = inputMap.get(colName);
            RuleInput ruleInput = this.fieldMap.get(colName);

            // Actual comparison is determined by the input types. So over to them.
            if (!ruleInput.evaluate(inputValue)) {
                return false;
            }
        }

        return true;
    }

    public boolean isConflicting(Rule rule) throws Exception {
        RuleSystemMetaData ruleSystemMetaData =
            RuleSystemMetaDataFactory.getInstance().getMetaData(this.ruleSystemName);

        for (RuleInputMetaData col : ruleSystemMetaData.getInputColumnList()) {
            String colName = col.getName();

            if (!colName.equals(ruleSystemMetaData.getUniqueIdColumnName()) &&
                    !colName.equals(ruleSystemMetaData.getUniqueOutputColumnName())) {
                RuleInput thisInput = this.fieldMap.get(colName);
                RuleInput ruleInput = rule.getColumnData(colName);

                // Mark as not conflicting if any field is not conflicting
                if (!ruleInput.isConflicting(thisInput)) {
                    return false;
                }
            }
        }

        return true;
    }

    public RuleInput getColumnData(String colName) {
        return this.fieldMap.get(colName);
    }

    /**
     * This method is used to modify the values of rule inputs in a rule. To
     * prevent someone from accidentally modifying column values which propagate
     * throughout the system, this method creates a copy of the current rule,
     * overwrites the specified column with the given value, and returns a new
     * rule. This keeps rule objects unmodifiable to a reasonable extent.
     *
     * @param colName
     * @param value
     * @return
     * @throws Exception
     */
    public Rule setColumnData(String colName, String value) throws Exception {
        Map<String, String> inputMap = new HashMap<>();
        for (Map.Entry<String, RuleInput> ruleInput : this.fieldMap.entrySet()) {
            String column = ruleInput.getKey();
            if (column.equals(colName)) {
                inputMap.put(column, value);
            } else {
                inputMap.put(column, ruleInput.getValue().getRawValue());
            }
        }

        return new Rule(this.ruleSystemName, inputMap);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("\n");

        for (RuleInput col : this.fieldMap.values()) {
            builder.append(col.toString());
        }

        return builder.toString();
    }
}
