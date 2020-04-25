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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NOT FOR ACTUAL USE!!!
 * This class shows how to initialize Rulette from a MySql store and its use. It takes the path of a file containing
 * MySQL data source properties (a sample file is in the resources folder of this module)
 *
 * @author Kislay Verma
 *
 */
public class SimpleMysqlUse implements Serializable {
    private static final long serialVersionUID = 6001113209922696345L;
    private static final String RULE_SYSTEM_NAME = "tax_rule_system";
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMysqlUse.class);

    public static void main(String[] args) throws Exception {
        SimpleMysqlUse example = new SimpleMysqlUse();
        if (args.length > 0) {
            example.run(args[0]);
        } else {
            // Edit this and put your property file path here
            example.run("/Users/kislayv/rulette-datasource.properties");
        }
    }

    public void run(String configFilePath) throws Exception {
        // Create a rule system with a properties file
        File f = new File(configFilePath);
        IDataProvider dataProvider1 = new MysqlDataProvider(f.getPath());
        RuleSystem rs1 = new RuleSystem(RULE_SYSTEM_NAME, dataProvider1);

        // Run all sample usage
        runSamples(rs1);

        // Create a rule system with properties
        IDataProvider dataProvider2 = new MysqlDataProvider(Utils.readProperties(configFilePath));
        RuleSystem rs2 = new RuleSystem(RULE_SYSTEM_NAME, dataProvider2);

        // Run all sample usage
        runSamples(rs2);
    }

    private void runSamples(RuleSystem rs) throws Exception {
        // Print all column names
        rs.getMetaData().getInputColumnList().forEach(r ->LOGGER.info(r.getName()));

        // Get rule by id
        Rule rule = rs.getRule("192");
        LOGGER.info("Rule : " + rule.toString());

        // Access values of all fields in a rule
        printRuleValues(rs, rule);

        // Get applicable rule for a map of rule inputs
        LOGGER.info(rs.getRule(getEvaluationInput()).toString());

        // Get all applicable rules for a map of rule inputs
        LOGGER.info(rs.getAllApplicableRules(getEvaluationInput()).toString());

        // Add a dummy rule
        LOGGER.info("==========Adding a dummy rule==========");
        Map<String, String> inputMap = new HashMap<>();
        rs.getMetaData().getInputColumnList().forEach(col -> {
            if (col.getName().equals("source_state")) {
                inputMap.put(col.getName(), "dummy");
            } else if (col.getName().equals("mrp_threshold")) {
                inputMap.put(col.getRangeLowerBoundFieldName(), "0");
                inputMap.put(col.getRangeUpperBoundFieldName(), "100");
            } else {
                inputMap.put(col.getName(), "");
            }
        });
        inputMap.put(rs.getMetaData().getUniqueOutputColumnName(), "4");
        Rule allEmptyRule = rs.addRule(inputMap);
        LOGGER.info("Added dummy rule : " + allEmptyRule);

        // Delete dummy rule
        LOGGER.info("==========Deleting the dummy rule==========");
        rs.deleteRule(allEmptyRule.getId());

        // Add rule (adding an existing rule fails with a conflict)
        LOGGER.info("==========Adding a conflicting rule==========");
        try {
            rule = rs.addRule(rule);
        } catch (Exception e) {
            LOGGER.error("Conflict in adding rule : " + e);
        }

        // See which rules are conflicting
        LOGGER.info("==========List conflicting rules==========");
        rs.getConflictingRules(rule).forEach(r -> {
            LOGGER.info(r.toString());
        });

        // Update a rule
        LOGGER.info("==========Update a rule==========");
        updateRule(rs, "192");
    }

    // How to access all field values of given rule
    private void printRuleValues(RuleSystem rs, Rule rule) throws Exception {
        LOGGER.info("Unique Input Column Value : " + rule.getColumnData(rs.getMetaData().getUniqueIdColumnName()));
        rs.getMetaData().getInputColumnList().forEach(col -> {
            LOGGER.info(col.getName() + " : " + rule.getColumnData(col.getName()));
        });
        LOGGER.info("Output Column Value : " + rule.getColumnData(rs.getMetaData().getUniqueOutputColumnName()));
    }

    private void updateRule(RuleSystem rs, String ruleId) throws Exception {
        Rule oldRule = rs.getRule(ruleId);
        Rule newRule = rs.getRule(ruleId);
        newRule = newRule
            .setColumnData("source_state", "PB")
            .setColumnData("item_type", "30");

        newRule = rs.updateRule(oldRule, newRule);
        LOGGER.info("Updated returned rule : " + newRule); // Returned rule has new values
        Rule updatedRule = rs.getRule(ruleId); // Stored rule has new values
        LOGGER.info("Updated rule in DB: " + updatedRule);
    }

    private  Map<String, String> getEvaluationInput() {
        Map<String, String> inputMap = new HashMap<>();
        inputMap.put("item_type", "3");
        inputMap.put("source_state", "WES");
        inputMap.put("mrp_threshold", "10000.00");

        return inputMap;
    }
}
