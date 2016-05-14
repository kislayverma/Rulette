package com.github.kislayverma.rulette.core.dao;

import com.github.kislayverma.rulette.core.rule.Rule;
import java.sql.SQLException;
import java.util.List;

public interface RuleSystemDao {
    List<Rule> getAllRules(String ruleSystemName) throws SQLException, Exception;

    Rule saveRule(String ruleSystemName, Rule rule) throws SQLException, Exception;

    boolean deleteRule(String ruleSystemName, Rule rule) throws SQLException, Exception;

    Rule updateRule(String ruleSystemName, Rule rule) throws SQLException, Exception;
}
