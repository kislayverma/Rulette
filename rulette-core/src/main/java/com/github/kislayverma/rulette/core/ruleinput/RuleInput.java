package com.github.kislayverma.rulette.core.ruleinput;

import com.github.kislayverma.rulette.core.metadata.RuleInputMetaData;
import com.github.kislayverma.rulette.core.ruleinput.type.RuleInputType;
import com.github.kislayverma.rulette.core.ruleinput.type.ValueInput;
import com.github.kislayverma.rulette.core.ruleinput.type.RangeInput;
import java.io.Serializable;

public abstract class RuleInput implements Serializable {

    protected RuleInputMetaData metaData;
    protected String rawInput;

    public static RuleInput createRuleInput(
        int id, String name, int priority, RuleInputType ruleType, 
            String dataType, String value, String rangeLowerBound, String rangeUpperBound) throws Exception {

        value = value == null ? "" : value;

        RuleInput r;
        switch (ruleType) {
            case VALUE:
                r = new ValueInput(id, name, priority, dataType, value);
                break;
            case RANGE:
                r = new RangeInput(id, name, priority, dataType, rangeLowerBound, rangeUpperBound);
                break;
            default:
                return null;
        }
        
        r.rawInput = value;

        return r;
    }

    public abstract boolean evaluate(String value) throws Exception;

    public abstract boolean isConflicting(RuleInput input) throws Exception;

    public final String getRawValue() {
        return this.rawInput;
    }

    public int getId() {
        return this.metaData.getId();
    }

    public String getName() {
        return this.metaData.getName();
    }

    public int getPriority() {
        return this.metaData.getPriority();
    }

    public RuleInputType getRuleInputType() {
        return this.metaData.getRuleInputType();
    }

    public String getRuleInputDataType() {
        return this.metaData.getDataType();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.metaData.getName())
                .append(":")
                .append(this.getRawValue())
                .append("\t");
        return builder.toString();
    }
}
