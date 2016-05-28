package com.github.kislayverma.rulette.core.ruleinput.value;

public abstract class RuleInputValue {

    private final String dataType;
    public RuleInputValue(String dataType) {
        this.dataType = dataType;
    }

    public String getDataType() {
        return dataType;
    }
}
