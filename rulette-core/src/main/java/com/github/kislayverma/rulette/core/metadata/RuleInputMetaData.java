package com.github.kislayverma.rulette.core.metadata;

import com.github.kislayverma.rulette.core.ruleinput.type.RuleInputType;
import java.io.Serializable;

public class RuleInputMetaData implements Serializable {
    private final int id;
    private final String name;
    private final int priority;
    private final RuleInputType ruleInputType;
    private final String dataType;
    private final String rangeLowerBound;
    private final String rangeUpperBound;

    public RuleInputMetaData(int id, String name, int priority, RuleInputType ruleInputType, String dataType) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.ruleInputType = ruleInputType;
        this.dataType = dataType;
        this.rangeLowerBound = null;
        this.rangeUpperBound = null;
    }

    public RuleInputMetaData(int id, String name, int priority, RuleInputType ruleInputType, String dataType, String rangeLowerBound, String rangeUpperBound) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.ruleInputType = ruleInputType;
        this.dataType = dataType;
        this.rangeLowerBound = rangeLowerBound;
        this.rangeUpperBound = rangeUpperBound;
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

    public String getRangeLowerBound() {
        return rangeLowerBound;
    }

    public String getRangeUpperBound() {
        return rangeUpperBound;
    }
}
