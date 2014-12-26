package rulesystem.ruleinput.rulevalue;

import java.io.Serializable;

public class RuleInputNumberValue extends RuleInputValue implements Serializable {

    private final Double value;
    private final String stringValue;

    public RuleInputNumberValue (String value) throws Exception {
        this.stringValue = value;
        this.dataType = RuleInputDataType.NUMBER;
        this.value = value == null || value.isEmpty() ? null : Double.parseDouble(value);
    }

    @Override
    public String getStringValue() {
        return this.stringValue;
    }

    @Override
    public boolean isEmpty() {
        return this.value == null;
    }

    @Override
    public int compareTo(String o) {
        Double otherValue = null;
        if (o != null) {
            otherValue = Double.parseDouble(o);
        }

        if (value == null && otherValue == null) {
            return 0;
        } else if (value == null && otherValue != null) {
            return -1;
        } else if (value != null && otherValue == null) {
            return 1;
        }

        return value.compareTo(otherValue);
    }
}
