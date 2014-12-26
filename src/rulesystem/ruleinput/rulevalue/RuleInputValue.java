package rulesystem.ruleinput.rulevalue;

import java.io.Serializable;

public abstract class RuleInputValue implements Serializable, Comparable<String> {

    protected RuleInputDataType dataType;

    public static RuleInputValue createRuleInputValue (RuleInputDataType ruleDataType, String value) throws Exception {
        switch (ruleDataType) {
            case NUMBER:
                return new RuleInputNumberValue(value);
            case DATE:
                return new RuleInputDateValue(value);
            case STRING:
                return new RuleInputStringValue(value);
            default:
                return null;
        }
    }

    public abstract boolean isEmpty();
    public abstract String getStringValue();
}
