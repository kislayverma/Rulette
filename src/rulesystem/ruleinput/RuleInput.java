package rulesystem.ruleinput;

import java.io.Serializable;
import rulesystem.ruleinput.rulevalue.RuleInputDataType;
import rulesystem.ruleinput.rulevalue.RuleInputValue;

public abstract class RuleInput implements Serializable {

    protected RuleInputMetaData metaData;
    protected RuleInputValue value;

    public static RuleInput createRuleInput(
        int id, int ruleSystemId, String name, int priority, RuleType ruleType, 
        RuleInputDataType dataType, String value) throws Exception {
        switch (ruleType) {
            case VALUE:
                return new ValueInput(id, ruleSystemId, name, priority, dataType, value);
            case RANGE:
                return new RangeInput(id, ruleSystemId, name, priority, dataType, value);
            default:
                return null;
        }
    }

    public abstract boolean evaluate(String value) throws Exception;

    public abstract boolean isConflicting(RuleInput input) throws Exception;

    public abstract String getValue();

    public int getId() {
        return this.metaData.getId();
    }

    public int getRuleSystemId() {
        return this.metaData.getRuleSystemId();
    }

    public String getName() {
        return this.metaData.getName();
    }

    public int getPriority() {
        return this.metaData.getPriority();
    }

    public RuleType getDataType() {
        return this.metaData.getRuleType();
    }
}
