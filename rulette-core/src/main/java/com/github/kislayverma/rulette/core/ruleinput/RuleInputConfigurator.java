package com.github.kislayverma.rulette.core.ruleinput;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuleInputConfigurator {
    private final Map<String, RuleInputConfiguration> configMap = new ConcurrentHashMap<>();

    public void addConfiguration(RuleInputConfiguration config) throws Exception {
        RuleInputConfiguration existingConfig = configMap.get(config.getRuleInputName());
        if (existingConfig != null) {
            throw new Exception("Config already present for input name " + config.getRuleInputName());
        } else {
            configMap.put(config.getRuleInputName(), config);
        }
    }

    public RuleInputConfiguration getConfig(String ruleInputName) {
        return this.configMap.get(ruleInputName);
    }
}
