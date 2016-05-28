package com.github.kislayverma.rulette.core.ruleinput.value;

public abstract class RuleInputValue {

    private String dataType;
    public RuleInputValue(String dataType) {
        this.dataType = dataType;
    }

    public String getDataType() {
        return dataType;
    }
//
//    public static IInputValue createRuleInputValue (RuleInputDataType ruleDataType, String value) throws Exception {
//        switch (ruleDataType) {
//            case NUMBER:
//                return new InputNumberValue(value);
//            case DATE:
//                return new InputDateValue(value);
//            case STRING:
//                return new InputStringValue(value);
//            default:
//                return null;
//        }
//    }
}
