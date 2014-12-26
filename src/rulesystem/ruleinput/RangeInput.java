package rulesystem.ruleinput;

import java.io.Serializable;
import rulesystem.ruleinput.rulevalue.RuleInputDataType;
import rulesystem.ruleinput.rulevalue.RuleInputValue;

public class RangeInput extends RuleInput implements Serializable {

    private RuleInputValue lowerBound;
    private RuleInputValue upperBound;

    public RangeInput(int id, int ruleSystemId, String name, int priority, RuleInputDataType inputDataType, String value)
            throws Exception {
        this.metaData = new RuleInputMetaData(id, ruleSystemId, name, priority, RuleType.VALUE, inputDataType);
        String[] rangeArr = value.split("-");

        if (value == null || value.isEmpty()) {
            // The'any' case
            this.lowerBound = RuleInputValue.createRuleInputValue(metaData.getRuleDataType(), "");
            this.upperBound = RuleInputValue.createRuleInputValue(metaData.getRuleDataType(), "");
        } else if (rangeArr.length < 2) {
            throw new Exception("Improper value for field " + this.metaData.getName()
                    + ". Range fields must be given in the format 'a-b' (with "
                    + "a and b as inclusive lower and upper bound respectively.)");
        } else {
            this.lowerBound = RuleInputValue.createRuleInputValue(metaData.getRuleDataType(), rangeArr[0] == null ? "" : rangeArr[0]);
            this.upperBound = RuleInputValue.createRuleInputValue(metaData.getRuleDataType(), rangeArr[0] == null ? "" : rangeArr[1]);
        }
    }

    @Override
    public boolean evaluate(String value) throws Exception {
        if (lowerBound.isEmpty() && upperBound.isEmpty()) {
            return true;
        }

        if (lowerBound.compareTo(value) <= 0 && upperBound.compareTo(value) >= 0) {
            return true;
        }

        return false;
    }

    @Override
    public String getValue() {
        return lowerBound.isEmpty() && upperBound.isEmpty()
                ? "" : lowerBound.getStringValue() + "-" + upperBound.getStringValue();
    }

    /**
     * The input rule input conflicts with this if the ranges specified by the
     * two are overlapping.
     *
     * @throws Exception
     */
    @Override
    public boolean isConflicting(RuleInput input) throws Exception {
        if (!input.getDataType().equals(this.getDataType())) {
            throw new Exception("Compared rule inputs '" + this.getName() + "' and '"
                    + input.getName() + "' are not the same type.");
        }

        String inputVal = input.getValue();

        // If values are same, or both are 'Any'
        if (this.getValue().equals(inputVal)) {
            return true;
        }

        // If only one is 'Any', there is no conflict
        if ("".equals(this.getValue()) || "".equals(inputVal)) {
            return false;
        }

        String[] inputArr = inputVal.split("-");

        if ((this.lowerBound.compareTo(inputArr[0]) < 0 && this.upperBound.compareTo(inputArr[0]) <= 0)
                || (this.lowerBound.compareTo(inputArr[1]) > 0 && this.upperBound.compareTo(inputArr[1]) > 0)) {
            return false;
        }

        return true;
    }
}
