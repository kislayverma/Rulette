package com.github.kislayverma.rulette.example.mysql;

import com.github.kislayverma.rulette.RuleSystem;
import com.github.kislayverma.rulette.core.rule.Rule;
import com.github.kislayverma.rulette.core.data.IDataProvider;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.github.kislayverma.rulette.mysql.MysqlDataProvider;
import java.util.List;

/**
 * NOT FOR ACTUAL USE!!!
 * This class shows how to initialize a Rulette engine from MySql store and its use.
 *
 * @author Kislay Verma
 *
 */
public class SimpleMysqlUse implements Serializable {
    private static final long serialVersionUID = 6001113209922696345L;

    public static void main(String[] args) throws Exception {
        File f = new File("/Users/kislay.verma/Applications/apache-tomcat-7.0.53/conf/rulette-datasource.properties");
        IDataProvider dataProvider = new MysqlDataProvider(f.getPath());
        RuleSystem rs = new RuleSystem("vat_rule_system", dataProvider);

        Map<String, String> inputMap = new HashMap<>();
        inputMap.put("item_type", "10");
        inputMap.put("source_state", "DEL");
//        inputMap.put("material", "platinum");
        inputMap.put("mrp_threshold", "10000.00");

        // Rule rule = null;
        long stime = new Date().getTime();
        for (int i = 0; i < 1; i++) {
            Rule rule = rs.getRule(inputMap);
            System.out.println("Rule found : " + ((rule == null) ? "none" : rule.toString()));
            System.out.println("\n---------------------------\n");
            List<Rule> allApplicableRules = rs.getAllApplicableRules(inputMap);
            System.out.println((allApplicableRules == null) ? "none" : allApplicableRules.toString());
//            System.out.println("\n---------------------------\n");
//            List<Rule> allRules = rs.getAllRules();
//            System.out.println((allRules == null) ? "none" : allRules.toString());
        }

        long etime = new Date().getTime();
        System.out.println("Time taken to get rule : " + (etime - stime) + " ms.");
    }
}
