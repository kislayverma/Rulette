package com.github.kislayverma.rulette.core.ruleinput.value;

public abstract class RuleInputValue {

    protected RuleInputDataType dataType;

    public static IInputValue createRuleInputValue (RuleInputDataType ruleDataType, String value) throws Exception {
        switch (ruleDataType) {
            case NUMBER:
                return new InputNumberValue(value);
            case DATE:
                return new InputDateValue(value);
            case STRING:
                return new InputStringValue(value);
            default:
                return null;
        }
    }
}
