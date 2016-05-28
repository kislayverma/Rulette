package com.github.kislayverma.rulette.core.ruleinput;

import com.github.kislayverma.rulette.core.ruleinput.value.RuleInputDataType;
import java.io.Serializable;

public class RuleInputMetaData implements Serializable {
    private final int id;
    private final String name;
    private final int priority;
    private final RuleInputType ruleInputType;
    private final RuleInputDataType ruleInputDataType;

    public RuleInputMetaData(int id, String name, int priority, RuleInputType ruleType, RuleInputDataType ruleDataType)
            throws Exception {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.ruleInputType = ruleType;
        this.ruleInputDataType = ruleDataType;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public RuleInputType getRuleInputType() {
        return ruleInputType;
    }

    public RuleInputDataType getRuleInputDataType() {
        return ruleInputDataType;
    }
}
