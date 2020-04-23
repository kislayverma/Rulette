package com.github.kislayverma.rulette.example.mysql;

import com.github.kislayverma.rulette.RuleSystem;
import com.github.kislayverma.rulette.core.rule.Rule;
import com.github.kislayverma.rulette.core.data.IDataProvider;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.github.kislayverma.rulette.mysql.MysqlDataProvider;
import com.github.kislayverma.rulette.mysql.util.Utils;

/**
 * NOT FOR ACTUAL USE!!!
 * This class shows how to initialize Rulette from a MySql store and its use.
 *
 * @author Kislay Verma
 *
 */
public class SimpleMysqlUse implements Serializable {
    private static final long serialVersionUID = 6001113209922696345L;
    private static final String PROPERTIES_FILE_PATH = "/Users/kislayv/rulette-datasource.properties";
    private static final String RULE_SYSTEM_NAME = "vat_rule_system";

    public static void main(String[] args) throws Exception {
        SimpleMysqlUse example = new SimpleMysqlUse();
        example.run();
    }

    public void run() throws Exception {
        // Create a rule system with a properties file
        File f = new File(PROPERTIES_FILE_PATH);
        IDataProvider dataProvider1 = new MysqlDataProvider(f.getPath());
        RuleSystem rs1 = new RuleSystem(RULE_SYSTEM_NAME, dataProvider1);

        // Run all sample usage
        runSamples(rs1);

        // Create a rule system with properties
        IDataProvider dataProvider2 = new MysqlDataProvider(Utils.readProperties(PROPERTIES_FILE_PATH));
        RuleSystem rs2 = new RuleSystem(RULE_SYSTEM_NAME, dataProvider2);

        // Run all sample usage
        runSamples(rs2);
    }

    private void runSamples(RuleSystem rs) throws Exception {
        // Get rule by id
        Rule rule = rs.getRule("192");
        System.out.println("Rule : " + rule.toString());

        // Access values of all fields in a rule
        printRuleValues(rs, rule);

        // Get applicable rule for a map of rule inputs
        System.out.println(rs.getRule(getEvaluationInput()));

        // Get all applicable rules for a map of rule inputs
        System.out.println(rs.getAllApplicableRules(getEvaluationInput()));

        // Add rule (adding an existing rule fails with a conflict)
        rule = rs.addRule(rule);

        // See which rules are conflicting
        rs.getConflictingRules(rule).forEach(r -> {
            System.out.println(r);
        });

        // Update a rule
        updateRule(rs, "192");
    }

    // How to access all field values of given rule
    private void printRuleValues(RuleSystem rs, Rule rule) throws Exception {
        System.out.println("Unique Input Column Value : " + rule.getColumnData(rs.getMetaData().getUniqueIdColumnName()));
        rs.getMetaData().getInputColumnList().forEach(col -> {
            System.out.println(col.getName() + " : " + rule.getColumnData(col.getName()));
        });
        System.out.println("Output Column Value : " + rule.getColumnData(rs.getMetaData().getUniqueOutputColumnName()));
    }

    private void updateRule(RuleSystem rs, String ruleId) throws Exception {
        Rule oldRule = rs.getRule(ruleId);
        Rule newRule = rs.getRule(ruleId);
        newRule = newRule
            .setColumnData("source_state", "PB")
            .setColumnData("item_type", "30");

        newRule = rs.updateRule(oldRule, newRule);
        printRuleValues(rs, newRule); // Returned rule has new values
        Rule updatedRule = rs.getRule(ruleId); // Stored rule has new values
        printRuleValues(rs, updatedRule);
    }

    private  Map<String, String> getEvaluationInput() {
        Map<String, String> inputMap = new HashMap<>();
        inputMap.put("item_type", "10");
        inputMap.put("source_state", "DEL");
        inputMap.put("mrp_threshold", "10000.00");

        return inputMap;
    }
}
