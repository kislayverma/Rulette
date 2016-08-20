package com.github.kislayverma.rulette.rest.controller;

import com.github.kislayverma.rulette.RuleSystem;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RuleSystemFactory {
    private static final Map<String, RuleSystem> RULE_SYSTEM_MAP = new ConcurrentHashMap<>();

    public RuleSystem getRuleSystem(String ruleSystemName) {
        if (ruleSystemName == null) {
            throw new RuntimeException("Rule system name not provided");
        }

        RuleSystem rs = RULE_SYSTEM_MAP.get(ruleSystemName);
        if (rs != null) {
            return rs;
        } else {
            synchronized(RULE_SYSTEM_MAP) {
                try {
                    rs = loadRuleSystem(ruleSystemName);
                    if (rs != null) {
                        RULE_SYSTEM_MAP.put(ruleSystemName, rs);
                        return rs;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw new RuntimeException("Rule system with name " + ruleSystemName + " not defined");
                }
            }
        }

        return null;
    }

    public void reloadRuleSystem(String ruleSystemName) {
        synchronized(RULE_SYSTEM_MAP) {
            RULE_SYSTEM_MAP.remove(ruleSystemName);
        }
    }

    private RuleSystem loadRuleSystem(String ruleSystemName) throws Exception {
        System.out.print("Loading ruLe system with name " + ruleSystemName);
        return new RuleSystem(ruleSystemName, null);
    }
}
