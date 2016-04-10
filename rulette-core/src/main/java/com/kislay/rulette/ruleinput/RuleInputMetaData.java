package com.kislay.rulette.ruleinput;

import com.kislay.rulette.ruleinput.value.InputDataType;
import java.io.Serializable;

public class RuleInputMetaData implements Serializable {
    private final int id;
    private final String name;
    private final int priority;
    private final RuleType ruleType;
    private final InputDataType ruleDataType;

    public RuleInputMetaData(int id, String name, int priority, RuleType ruleType, InputDataType ruleDataType)
            throws Exception {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.ruleType = ruleType;
        this.ruleDataType = ruleDataType;
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

    public RuleType getRuleType() {
        return ruleType;
    }

    public InputDataType getRuleDataType() {
        return ruleDataType;
    }
}
