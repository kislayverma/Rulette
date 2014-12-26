package rulesystem.ruleinput;

import java.io.Serializable;
import rulesystem.ruleinput.rulevalue.RuleInputDataType;
import rulesystem.ruleinput.rulevalue.RuleInputValue;

public class ValueInput extends RuleInput implements Serializable {

    public ValueInput(int id, int ruleSystemId, String name, int priority, RuleInputDataType inputDataType, String value)
            throws Exception {
        this.metaData = new RuleInputMetaData(id, ruleSystemId, name, priority, RuleType.VALUE, inputDataType);
        this.value = RuleInputValue.createRuleInputValue(inputDataType, value == null ? "" : value);
    }

    @Override
    public boolean evaluate(String value) throws Exception {
        return this.value.equals(RuleInputValue.createRuleInputValue(metaData.getRuleDataType(), value == null ? "" : value));
    }

    @Override
    public String getValue() {
        return value.getStringValue();
    }

    /**
     * The given input conflicts with this if the values are same.
     *
     * @throws Exception
     */
    @Override
    public boolean isConflicting(RuleInput input) throws Exception {
        if (!input.getDataType().equals(this.getDataType())) {
            throw new Exception("Compared rule inputs '" + this.getName() + "' and '"
                    + input.getName() + "' are not the same type.");
        }

        return input.getValue().equals(this.value) ? true : false;
    }
}
