package com.github.kislayverma.rulette.core.metadata;

import com.github.kislayverma.rulette.core.ruleinput.type.RuleInputType;
import java.io.Serializable;

/**
 * This class represents the rule input entity model
 */
public class RuleInputMetaData implements Serializable {
    private static final long serialVersionUID = 7018331311799000825L;
    private final String name;
    private final int priority;
    private final RuleInputType ruleInputType;
    private final String dataType;
    private final String rangeLowerBoundFieldName;
    private final String rangeUpperBoundFieldName;

    public RuleInputMetaData(String name, int priority, RuleInputType ruleType, String dataType,
            String rangeLowerBoundFieldName, String rangeUpperBoundFieldName) {
        this.name = name;
        this.priority = priority;
        this.ruleInputType = ruleType;
        this.dataType = dataType;
        this.rangeLowerBoundFieldName = rangeLowerBoundFieldName;
        this.rangeUpperBoundFieldName = rangeUpperBoundFieldName;
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

    public String getRangeLowerBoundFieldName() {
        return rangeLowerBoundFieldName;
    }

    public String getRangeUpperBoundFieldName() {
        return rangeUpperBoundFieldName;
    }
}
