package rulesystem.ruleinput.rulevalue;

import java.io.Serializable;

public class RuleInputStringValue extends RuleInputValue implements Serializable {

    private final String value;

    public RuleInputStringValue (String value) throws Exception {
        this.dataType = RuleInputDataType.STRING;
        this.value = value;
    }

    @Override
    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }

    @Override
    public String getStringValue() {
        return this.value;
    }

    @Override
    public int compareTo(String o) {
        return value.compareTo(o);
    }
}
