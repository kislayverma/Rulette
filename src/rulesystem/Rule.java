package rulesystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rulesystem.ruleinput.RuleInput;
import rulesystem.ruleinput.RuleInputMetaData;
import rulesystem.ruleinput.RuleInputMetaData.DataType;

/**
 * This class models a rule in the rule system. It has input columns and an
 * output value which the rule system maps these inputs to.
 *
 * @author Kislay
 *
 */
public class Rule {

    private Map<String, RuleInput> fieldMap;
    // This list is to keep the order (priority order) of inputs
    private List<RuleInputMetaData> inputColumnList;
    private String uniqueIdColumnName = "id";
    private String uniqueOutputColumnName = "rule_output_id";
    private static final int UNIQUE_ID_INPUT_ID = -1;
    private static final int UNIQUE_OUTPUT_ID_INPUT_ID = -2;

    /**
     * This constructor takes the list of columns in the rule system and a map
     * of value to populate the fields of this rule. Any fields missing in the
     * input are set to blank (meaning 'Any').
     *
     * If you are extending the rule class, be sure to call this constructor.
     *
     * @param colNames
     * @param inputMap
     * @param uniqueIdColName [OPTIONAL] Name of the column containing unique id
     * for the rule. "id" will be used by default.
     * @param uniqueOutputColName [OPTIONAL] Name of the column containing the
     * output of the rule system. "rule_output_id" will be used by default.
     *
     * @throws Exception
     */
    public Rule(List<RuleInputMetaData> columns,
            Map<String, String> inputMap,
            String uniqueIdColName,
            String uniqueOutputColName) throws Exception {
        this.inputColumnList = columns;
        if (uniqueIdColName != null) {
            this.uniqueIdColumnName = uniqueIdColName;
        }
        if (uniqueOutputColName != null) {
            this.uniqueOutputColumnName = uniqueOutputColName;
        }

        fieldMap = new HashMap<String, RuleInput>();
        for (RuleInputMetaData col : columns) {
            String inputVal = inputMap.get(col.getName());
            this.fieldMap.put(col.getName(),
                    RuleInput.createRuleInput(col.getId(),
                    col.getRuleSystemId(),
                    col.getName(),
                    col.getPriority(),
                    col.getDataType(),
                    (inputVal == null) ? "" : inputVal));
        }

        String ruleId = inputMap.get(this.uniqueIdColumnName);
        this.fieldMap.put(this.uniqueIdColumnName,
                RuleInput.createRuleInput(UNIQUE_ID_INPUT_ID,
                inputColumnList.get(0).getRuleSystemId(),
                this.uniqueIdColumnName,
                UNIQUE_ID_INPUT_ID,
                DataType.VALUE,
                (ruleId == null) ? "" : ruleId));

        String ruleOutputId = inputMap.get(this.uniqueOutputColumnName);
        this.fieldMap.put(this.uniqueOutputColumnName,
                RuleInput.createRuleInput(UNIQUE_OUTPUT_ID_INPUT_ID,
                inputColumnList.get(0).getRuleSystemId(),
                this.uniqueOutputColumnName,
                UNIQUE_OUTPUT_ID_INPUT_ID,
                DataType.VALUE,
                (ruleOutputId == null) ? "" : ruleOutputId));
    }

    /**
     * This method accepts a column name to column value mapping and return if
     * the mapping is true for this rule. i.e. It returns true if this rule is
     * applicable for the input values and false otherwise.
     *
     * The method returns true if one the following criteria are met for each
     * column: 1. Both rule and input are equal (same value or both being 'Any')
     * 2. Rule is any.
     *
     * In all other cases false is returned.
     *
     * @param inputMap
     * @return
     */
    public boolean evaluate(Map<String, String> inputMap) {
        // For each input column in order, get the value from the rule and compare against input.
        for (RuleInputMetaData col : this.inputColumnList) {
            String colName = col.getName();

            if (colName.equals(this.uniqueIdColumnName)
                    || colName.equals(this.uniqueOutputColumnName)) {
                continue;
            }

            String inputValue = inputMap.get(colName);
            RuleInput ruleInput = this.fieldMap.get(colName);

            // Actual comparison is determined by the input types. So over to them.
            if (ruleInput.evaluate(inputValue)) {
                continue;
            } else {
                return false;
            }
        }

        return true;
    }

    public boolean isConflicting(Rule rule) throws Exception {
        for (RuleInputMetaData col : this.inputColumnList) {
            String colName = col.getName();

            if (colName.equals(this.uniqueIdColumnName)
                    || colName.equals(this.uniqueOutputColumnName)) {
                continue;
            }

            RuleInput thisInput = this.fieldMap.get(colName);
            RuleInput ruleInput = rule.getColumnData(colName);

            // Mark as not conflicting if any field is not conflicting
            if (!ruleInput.isConflicting(thisInput)) {
                return false;
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
                inputMap.put(column, ruleInput.getValue().getValue());
            }
        }

        return new Rule(inputColumnList, inputMap, uniqueIdColumnName, uniqueOutputColumnName);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append(this.uniqueIdColumnName)
                .append(":")
                .append(getColumnData(this.uniqueIdColumnName).getValue())
                .append("\t");

        for (RuleInputMetaData col : this.inputColumnList) {
            builder.append(col.getName())
                    .append(":")
                    .append(getColumnData(col.getName()).getValue())
                    .append("\t");
        }

        builder.append(this.uniqueOutputColumnName)
                .append(":")
                .append(getColumnData(this.uniqueOutputColumnName).getValue())
                .append("\t");

        return builder.toString();
    }
}
