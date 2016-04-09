package rulette.ruleinput;

import java.io.Serializable;
import rulette.ruleinput.value.IInputValue;
import rulette.ruleinput.value.InputDataType;
import rulette.ruleinput.value.RuleInputValue;

public class ValueInput extends RuleInput implements Serializable {

    private IInputValue value;

    public ValueInput(int id, String name, int priority, InputDataType inputDataType, String value)
            throws Exception {
        this.metaData = new RuleInputMetaData(id, name, priority, RuleType.VALUE, inputDataType);
        this.value = RuleInputValue.createRuleInputValue(inputDataType, value == null ? "" : value);
    }

    @Override
    public boolean evaluate(String value) throws Exception {
        return this.value.compareTo(value) == 0;
    }

    /**
     * The given input conflicts with this if the values are same.
     *
     * @param input
     * @return 
     * @throws Exception
     */
    @Override
    public boolean isConflicting(RuleInput input) throws Exception {
        if (!input.getDataType().equals(this.getDataType())) {
            throw new Exception("Compared rule inputs '" + this.getName() + "' and '"
                    + input.getName() + "' are not the same type.");
        }

        return this.getRawValue().equals(input.getRawValue());
    }
}
