/*
 * Copyright 2016 kislay.verma.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.kislayverma.rulette.core.data;

import com.github.kislayverma.rulette.core.metadata.RuleInputMetaData;
import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaData;
import com.github.kislayverma.rulette.core.rule.Rule;

import java.util.List;

/**
 * This is the interface between Rulette engine and the rule storage layer. The engine uses this API to access and
 * modify rules and other metadata in the storage medium.
 *
 * @author kislay.verma
 */
public interface IDataProvider {
    default void addRuleInput(String ruleSystemName, RuleInputMetaData ruleInput) {
        throw new UnsupportedOperationException("Adding rule input not supported by this data provider implementation");
    }

    default void deleteRuleInput(String ruleSystemName, String ruleInputName) {
        throw new UnsupportedOperationException("Deleting rule input not supported by this data provider implementation");
    }

    RuleSystemMetaData getRuleSystemMetaData(String ruleSystemName);

    List<Rule> getAllRules(String ruleSystemName);

    Rule saveRule(String ruleSystemName, Rule rule);

    boolean deleteRule(String ruleSystemName, Rule rule);

    Rule updateRule(String ruleSystemName, Rule rule);
}
