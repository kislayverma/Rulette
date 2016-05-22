package com.github.kislayverma.rulette.core.ruleinput.value;

import org.joda.time.format.DateTimeFormatter;

public abstract class RuleInputValue {

    protected InputDataType dataType;

    public static IInputValue createRuleInputValue (InputDataType ruleDataType, String value) throws Exception {
        return createRuleInputValue(ruleDataType, value, null);
    }

    public static IInputValue createRuleInputValue (InputDataType ruleDataType, String value, DateTimeFormatter formatter) throws Exception {
        if (ruleDataType != InputDataType.DATE && formatter != null) {
            throw new RuntimeException("Formatters supported for only date inputs");
        }
        switch (ruleDataType) {
            case NUMBER:
                return new InputNumberValue(value);
            case DATE:
                return new InputDateValue(value, formatter);
            case STRING:
                return new InputStringValue(value);
            default:
                return null;
        }
    }
}
