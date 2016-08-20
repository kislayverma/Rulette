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

import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaData;
import com.github.kislayverma.rulette.core.rule.Rule;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author kislay.verma
 */
public interface IDataProvider {
    RuleSystemMetaData getRuleSystemMetaData(String ruleSystemName) throws Exception;

    List<Rule> getAllRules(String ruleSystemName) throws SQLException, Exception;

    Rule saveRule(String ruleSystemName, Rule rule) throws SQLException, Exception;

    boolean deleteRule(String ruleSystemName, Rule rule) throws SQLException, Exception;

    Rule updateRule(String ruleSystemName, Rule rule) throws SQLException, Exception;
}
