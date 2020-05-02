package com.github.kislayverma.rulette.core.rule;

import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaData;
import com.github.kislayverma.rulette.core.ruleinput.RuleInput;
import com.github.kislayverma.rulette.core.metadata.RuleInputMetaData;
import com.github.kislayverma.rulette.core.ruleinput.type.RangeInput;
import com.github.kislayverma.rulette.core.ruleinput.type.ValueInput;
import com.github.kislayverma.rulette.core.ruleinput.value.DefaultDataType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class models a rule in the rule system. It has input columns and an
 * output value which the rule system maps these inputs to.
 *
 * @author Kislay
 *
 */
public class Rule implements Serializable {
    private static final long serialVersionUID = 7025452883240080627L;

    private final RuleSystemMetaData ruleSystemMetaData;
    private final Map<String, RuleInput> fieldMap;
    private static final int DUMMY_PRIORITY_FOR_INPUT_AND_OUTPUT_ID_COL = -1;

    /**
     * This constructor takes the list of columns in the rule system and a map
     * of value to populate the fields of this rule. Any fields missing in the
     * input are set to blank (meaning 'Any').
     *
     * @param ruleSystemMetaData Meta data of the rule system to which this rule belongs
     * @param inputMap input values for constructing the rule
     */
    public Rule(RuleSystemMetaData ruleSystemMetaData, Map<String, String> inputMap) {
        this.ruleSystemMetaData = ruleSystemMetaData;
        this.fieldMap = new ConcurrentHashMap<>();

        // Construct all rule inputs
        for (RuleInputMetaData input : ruleSystemMetaData.getInputColumnList()) {
            String inputVal = inputMap.get(input.getName());
            inputVal = (inputVal == null) ? "" : inputVal;
            RuleInput ruleInput;
            switch (input.getRuleInputType()) {
                case RANGE:
                    ruleInput = new RangeInput(
                        input.getName(),
                        input.getPriority(), 
                        input.getDataType(),
                        inputMap.get(input.getRangeLowerBoundFieldName()),
                        inputMap.get(input.getRangeUpperBoundFieldName()));
                    break;
                case VALUE:
                    ruleInput = new ValueInput(input.getName(), input.getPriority(), input.getDataType(), inputVal);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported rule input type " + input.getRuleInputType());
            }

            this.fieldMap.put(input.getName(), ruleInput);
        }

        // Construct rule input object representing unique id
        String ruleId = inputMap.get(ruleSystemMetaData.getUniqueIdColumnName());
        this.fieldMap.put(ruleSystemMetaData.getUniqueIdColumnName(),
            new ValueInput(
            ruleSystemMetaData.getUniqueIdColumnName(),
            DUMMY_PRIORITY_FOR_INPUT_AND_OUTPUT_ID_COL,
            DefaultDataType.STRING.name(),
            (ruleId == null) ? "" : ruleId));

        // Construct rule input object representing ouput column
        String ruleOutputId = inputMap.get(ruleSystemMetaData.getUniqueOutputColumnName());
        this.fieldMap.put(ruleSystemMetaData.getUniqueOutputColumnName(),
            new ValueInput(
            ruleSystemMetaData.getUniqueOutputColumnName(),
            DUMMY_PRIORITY_FOR_INPUT_AND_OUTPUT_ID_COL,
            DefaultDataType.STRING.name(),
            (ruleOutputId == null) ? "" : ruleOutputId));
    }

    public String getId() {
        return this.getColumnData(ruleSystemMetaData.getUniqueIdColumnName()).getRawValue();
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
     * @param inputMap rule input values for evaluation
     * @return true if input values match this rule
     */
    public boolean evaluate(Map<String, String> inputMap) {
        // For each input column in order, get the value from the rule and compare against input.
        for (RuleInputMetaData col : this.ruleSystemMetaData.getInputColumnList()) {
            String colName = col.getName();

            if (colName.equals(this.ruleSystemMetaData.getUniqueIdColumnName())
                    || colName.equals(this.ruleSystemMetaData.getUniqueOutputColumnName())) {
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

    /**
     * Returns true if the give rule conflicts with this rule.
     * @param rule input rule to be checked for conflict
     * @return true if input rule conflicts with this rule, false otherwise
     */
    public boolean isConflicting(Rule rule) {
        for (RuleInputMetaData col : this.ruleSystemMetaData.getInputColumnList()) {
            String colName = col.getName();

            if (!colName.equals(this.ruleSystemMetaData.getUniqueIdColumnName()) &&
                    !colName.equals(this.ruleSystemMetaData.getUniqueOutputColumnName())) {
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
     * rule. This keeps rule objects immutable to a reasonable extent.
     *
     * @param colName column name whose value is to be set
     * @param value the value to be set
     * @return New rule object with the new value set
     */
    public Rule setColumnData(String colName, String value) {
        Map<String, String> inputMap = new HashMap<>();
        for (Map.Entry<String, RuleInput> ruleInput : this.fieldMap.entrySet()) {
            String column = ruleInput.getKey();
            if (column.equals(colName)) {
                inputMap.put(column, value);
            } else {
                inputMap.put(column, ruleInput.getValue().getRawValue());
            }
        }

        return new Rule(this.ruleSystemMetaData, inputMap);
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
