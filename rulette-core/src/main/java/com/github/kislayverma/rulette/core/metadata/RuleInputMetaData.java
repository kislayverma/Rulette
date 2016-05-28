package com.github.kislayverma.rulette.core.metadata;

import com.github.kislayverma.rulette.core.ruleinput.type.RuleInputType;
import java.io.Serializable;

public class RuleInputMetaData implements Serializable {
    private final int id;
    private final String name;
    private final int priority;
    private final RuleInputType ruleInputType;
    private final String dataType;
//    private IInputValueBuilder builder;

    public RuleInputMetaData(int id, String name, int priority, RuleInputType ruleType, String dataType)
            throws Exception {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.ruleInputType = ruleType;
        this.dataType = dataType;
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

    public String getDataType() {
        return dataType;
    }
}
