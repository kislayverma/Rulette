package com.github.kislayverma.rulette.core.ruleinput;

import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;
import com.github.kislayverma.rulette.core.ruleinput.value.IInputValueBuilder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RuleInputValueFactory {
    private static RuleInputValueFactory INSTANCE;
    private final Map<String, IInputValueBuilder> builderMap;
    private static final Logger LOGGER = LoggerFactory.getLogger(RuleInputValueFactory.class);

    private RuleInputValueFactory() {
        LOGGER.info("Initializing input data type factory...");
        this.builderMap = new ConcurrentHashMap<>();
        LOGGER.info("Input data type factory initialized");
    }

    public static RuleInputValueFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RuleInputValueFactory();
        }

        return INSTANCE;
    }

    public IInputValue buildRuleInputVaue(String ruleInputName, String rawValue) {

        IInputValueBuilder builder = builderMap.get(ruleInputName);
        if (builder == null) {
            throw new IllegalArgumentException("No input value builder registered for input " + ruleInputName);
        } else {
            return builder.build(rawValue);
        }
    }

    public void registerRuleInputBuilder(String ruleInputName, IInputValueBuilder builder) {
        builderMap.put(ruleInputName, builder);
    }
}
