package com.github.kislayverma.rulette.example.mysql;

import com.github.kislayverma.rulette.RuleSystem;
import com.github.kislayverma.rulette.core.exception.RuleConflictException;
import com.github.kislayverma.rulette.core.metadata.RuleInputMetaData;
import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaData;
import com.github.kislayverma.rulette.core.rule.Rule;
import com.github.kislayverma.rulette.core.data.IDataProvider;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
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

    public static void main(String[] args) throws RuleConflictException, SQLException, IOException {
        SimpleMysqlUse example = new SimpleMysqlUse();
        if (args.length > 0) {
            example.run(args[0]);
        } else {
            // Edit this and put your property file path here
            example.run("/Users/kislayv/rulette-datasource.properties");
        }
    }

    public void run(String configFilePath) throws IOException, SQLException, RuleConflictException {
        // Create a rule system with a properties file
        File f = new File(configFilePath);
        IDataProvider dataProvider = new MysqlDataProvider(f.getPath());
        // Print all rule systems known to this provider
        dataProvider.getAllRuleSystemMetaData().forEach(rsmd -> LOGGER.info(rsmd.toString()));
        RuleSystem rs1 = new RuleSystem(RULE_SYSTEM_NAME, dataProvider);

        // Run all sample usage
        runSamples(rs1);

        // Create a new, identical rule system
        String newRuleSystemName = RULE_SYSTEM_NAME + "-1";
        RuleSystemMetaData newMetadata = new RuleSystemMetaData(
            newRuleSystemName,
            rs1.getMetaData().getTableName(),
            rs1.getMetaData().getUniqueIdColumnName(),
            rs1.getMetaData().getUniqueOutputColumnName(),
            rs1.getMetaData().getInputColumnList());
        RuleSystem.createNewRuleSystem(newMetadata, dataProvider);
        RuleSystem rs2 = new RuleSystem(newRuleSystemName, dataProvider);

        // Run all sample usage
        runSamples(rs2);

        // Delete the new rule system
        RuleSystem.deleteRuleSystem(newRuleSystemName, dataProvider);
    }

    private void runSamples(RuleSystem rs) throws RuleConflictException {
        // Print all column names
        rs.getMetaData().getInputColumnList().forEach(r ->LOGGER.info(r.getName()));

        // Get mrp_threshold rule input
        RuleInputMetaData ruleInput = rs.getMetaData().getInputColumnList()
            .stream()
            .filter(col->"mrp_threshold".equals(col.getName()))
            .findFirst()
            .get();
        // Adding and deleting rule input
        LOGGER.info("==========Deleting rule input : " + ruleInput.getName() +  "==========");
        rs.deleteRuleInput(ruleInput.getName());
        rs.reload();
        LOGGER.info("==========Adding back the same rule input : " + ruleInput.getName() +  "==========");
        rs.addRuleInput(ruleInput);
        rs.reload();

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

        LOGGER.info("==========Update a value type input in a rule==========");
        updateValueInput(rs, "192");

        LOGGER.info("==========Update a range type input in a rule==========");
        updateRangeInput(rs, "192");
    }

    // How to access all field values of given rule
    private void printRuleValues(RuleSystem rs, Rule rule) {
        LOGGER.info("Unique Input Column Value : " + rule.getColumnData(rs.getMetaData().getUniqueIdColumnName()));
        rs.getMetaData().getInputColumnList().forEach(col -> {
            LOGGER.info(col.getName() + " : " + rule.getColumnData(col.getName()));
        });
        LOGGER.info("Output Column Value : " + rule.getColumnData(rs.getMetaData().getUniqueOutputColumnName()));
    }

    private void updateValueInput(RuleSystem rs, String ruleId) throws RuleConflictException {
        Rule oldRule = rs.getRule(ruleId);
        String newValue = (oldRule.getColumnData("source_state").getRawValue().equals("PB")) ? "WES" : "PB";
        Rule newRule = oldRule.setColumnData("source_state", newValue);

        newRule = rs.updateRule(oldRule, newRule);
        LOGGER.info("Updated returned rule : " + newRule); // Returned rule has new values
        Rule updatedRule = rs.getRule(ruleId); // Stored rule has new values
        LOGGER.info("Updated rule in DB: " + updatedRule);
    }

    private void updateRangeInput(RuleSystem rs, String ruleId) throws RuleConflictException {
        Rule oldRule = rs.getRule(ruleId);
        Map<String, String> newValueMap = new HashMap<>();
        newValueMap.put("min_mrp", "0");
        newValueMap.put("max_mrp", "500");
        Rule newRule = oldRule.setColumnData("mrp_threshold", newValueMap);

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
