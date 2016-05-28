package com.github.kislayverma.rulette.core.ruleinput;

import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;
import com.github.kislayverma.rulette.core.ruleinput.value.IInputValueBuilder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuleInputValueFactory {
    private static RuleInputValueFactory INSTANCE;
    private final Map<String, IInputValueBuilder> builderMap;

    private RuleInputValueFactory() {
        System.out.println("Initializing input data type factory...");
        this.builderMap = new ConcurrentHashMap<>();
        System.out.println("Input data type factory initialized");
    }

    public static RuleInputValueFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RuleInputValueFactory();
        }

        return INSTANCE;
    }

    public IInputValue buildRuleInputVaue(String ruleInputName, String rawValue) throws Exception {

        IInputValueBuilder builder = builderMap.get(ruleInputName);
        if (builder == null) {
            throw new RuntimeException("No input value builder registered for input " + ruleInputName);
        } else {
            return builder.build(rawValue);
        }
    }

    public void registerRuleInputBuilder(String ruleInputName, IInputValueBuilder builder) {
        builderMap.put(ruleInputName, builder);
    }
}
