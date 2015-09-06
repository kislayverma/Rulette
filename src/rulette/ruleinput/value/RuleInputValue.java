package rulette.ruleinput.value;

public abstract class RuleInputValue {

    protected InputDataType dataType;

    public static IInputValue createRuleInputValue (InputDataType ruleDataType, String value) throws Exception {
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
