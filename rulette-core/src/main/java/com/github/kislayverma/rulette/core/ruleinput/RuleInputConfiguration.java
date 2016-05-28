package com.github.kislayverma.rulette.core.ruleinput;

import com.github.kislayverma.rulette.core.ruleinput.value.IInputValueBuilder;

public class RuleInputConfiguration {
    private final String ruleInputName;
    private final IInputValueBuilder inputValueBuilder;

    public RuleInputConfiguration(String ruleInputName, IInputValueBuilder inputValueBuilder) {
        this.ruleInputName = ruleInputName;
        this.inputValueBuilder = inputValueBuilder;
    }

    public String getRuleInputName() {
        return ruleInputName;
    }

    public IInputValueBuilder getInputValueBuilder() {
        return inputValueBuilder;
    }
}
