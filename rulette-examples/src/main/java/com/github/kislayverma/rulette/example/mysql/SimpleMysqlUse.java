package com.github.kislayverma.rulette.example.mysql;

import com.github.kislayverma.rulette.RuleSystem;
import com.github.kislayverma.rulette.core.data.IDataProvider;
import com.github.kislayverma.rulette.core.rule.Rule;
import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.github.kislayverma.rulette.mysql.MysqlDataProvider;

/**
 * NOT FOR ACTUAL USE!!!
 * This class shows how to initialize a Rulette engine from MySql store and its use.
 *
 * @author Kislay Verma
 *
 */
public class SimpleMysqlUse implements Serializable {

    public static void main(String[] args) throws Exception {
        File f = new File("rulette-datasource.properties");
        IDataProvider dataProvider = new MysqlDataProvider(f.getPath());
        RuleSystem rs = new RuleSystem("discount_rule_system", dataProvider);

        Map<String, String> inputMap = new HashMap<>();
        inputMap.put("article_type", "T Shirt");
        inputMap.put("brand", "Adidas");
        inputMap.put("is_active", "1");
        inputMap.put("style_id", "1");
        inputMap.put("valid_date_range", "20131130");

        // Rule rule = null;
        long stime = new Date().getTime();
        for (int i = 0; i < 1; i++) {
            Rule rule = rs.getRule(inputMap);
            System.out.println((rule == null) ? "none" : rule.toString());
//            System.out.println("\n---------------------------\n");
//            List<Rule> rules = rs.getAllApplicableRules(inputMap);
//            System.out.println((rules == null) ? "none" : rules.toString());
        }

        long etime = new Date().getTime();
        System.out.println("Time taken to get rule : " + (etime - stime) + " ms.");
    }
}
