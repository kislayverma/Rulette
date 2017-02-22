package com.github.kislayverma.rulette.engine.impl.trie;

import com.github.kislayverma.rulette.core.engine.IEvaluationEngine;
import com.github.kislayverma.rulette.core.metadata.RuleSystemMetaData;
import com.github.kislayverma.rulette.core.rule.Rule;
import com.github.kislayverma.rulette.core.metadata.RuleInputMetaData;
import com.github.kislayverma.rulette.core.ruleinput.RuleInput;
import com.github.kislayverma.rulette.core.ruleinput.type.RuleInputType;
import com.github.kislayverma.rulette.engine.impl.trie.node.Node;
import com.github.kislayverma.rulette.engine.impl.trie.node.RangeNode;
import com.github.kislayverma.rulette.engine.impl.trie.node.ValueNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation of the {@link IEvaluationEngine} interface uses a Trie to store and 
 * locate rules efficiently. The higher priority inputs constitute the top level nodes and
 * lower priority nodes are set as child nodes. The actual rule output value is found at the
 * leaf nodes.
 * 
 * @author kislay.verma
 */
public class TrieBasedEvaluationEngine implements IEvaluationEngine {

    private final RuleSystemMetaData metaData;
    private final Map<String, Rule> allRules = new ConcurrentHashMap<>();
    private final Node root;
    private static final Logger LOGGER = LoggerFactory.getLogger(TrieBasedEvaluationEngine.class);

    public TrieBasedEvaluationEngine(RuleSystemMetaData metaData) throws Exception {
        this(metaData, null);
    }

    public TrieBasedEvaluationEngine(RuleSystemMetaData metaData, List<Rule> rules) throws Exception {
        this.metaData = metaData;
        if (this.metaData.getInputColumnList().get(0).getRuleInputType().equals(RuleInputType.VALUE)) {
            this.root = new ValueNode(this.metaData.getInputColumnList().get(0).getName());
        } else {
            this.root = new RangeNode(this.metaData.getInputColumnList().get(0).getName());
        }
        
        if (rules != null) {
            for (Rule rule : rules) {
                addRule(rule);
            }
        }
    }

    @Override
    public List<Rule> getAllRules() {
        return new ArrayList<>(this.allRules.values());
    }

    @Override
    public Rule getRule(String ruleId) {
        if (ruleId == null) {
            return null;
        }

        return this.allRules.get(ruleId);
    }

    @Override
    public Rule getRule(Map<String, String> inputMap) throws Exception {
        List<Rule> eligibleRules = getAllApplicableRules(inputMap);
        if (eligibleRules != null && !eligibleRules.isEmpty()) {
            List<Rule> filteredRules = filterAllApplicableRules(eligibleRules, inputMap);
            if (!filteredRules.isEmpty()) {
                return filteredRules.get(filteredRules.size() - 1);
            }
        }

        return null;
    }

    @Override
    public Rule getNextApplicableRule(Map<String, String> inputMap) throws Exception {
        List<Rule> eligibleRules = getAllApplicableRules(inputMap);

        if (eligibleRules != null && eligibleRules.size() > 1) {
            List<Rule> filteredRules = filterAllApplicableRules(eligibleRules, inputMap);
            if (!filteredRules.isEmpty()) {
                return filteredRules.get(filteredRules.size() - 2);
            }
        }

        return null;
    }

    @Override
    public void addRule(Rule rule) throws Exception {
        Node currNode = this.root;
        for (int i = 0; i < metaData.getInputColumnList().size(); i++) {
            RuleInputMetaData currInput = metaData.getInputColumnList().get(i);

            // 1. See if the current node has a node mapping to the field value
            List<Node> nodeList =
                currNode.getNodesForAddingRule(rule.getColumnData(currInput.getName()));

            // 2. If it doesn't, create a new empty node and map the field value
            //    to the new node.
            //    Also move to the new node.
            if (nodeList.isEmpty()) {
                Node newNode;
                if (i < metaData.getInputColumnList().size() - 1) {
                    if (metaData.getInputColumnList().get(i + 1).getRuleInputType().equals(RuleInputType.VALUE)) {
                        newNode = new ValueNode(metaData.getInputColumnList().get(i + 1).getName());
                    } else {
                        newNode = new RangeNode(metaData.getInputColumnList().get(i + 1).getName());
                    }
                } else {
                    newNode = new ValueNode("");
                }

                currNode.addChildNode(rule.getColumnData(currInput.getName()), newNode);
                currNode = newNode;
            } // 3. If it does, move to that node.
            else {
                currNode = nodeList.get(0);
            }
        }

        currNode.setRule(rule);
        this.allRules.put(rule.getId(), rule);
    }

    @Override
    public void deleteRule(Rule rule) throws Exception {
        // Delete the rule from the map
        this.allRules.remove(rule.getId());

        // Locate and delete the rule from the trie
        Stack<Node> stack = new Stack<>();
        Node currNode = this.root;

        for (RuleInputMetaData rimd : metaData.getInputColumnList()) {
            String value = rule.getColumnData(rimd.getName()).getRawValue();
            value = (value == null) ? "" : value;

            Node nextNode = currNode.getMatchingRule(value);
            stack.push(currNode);

            currNode = nextNode;
        }

        if (!currNode.getRule().getColumnData(metaData.getUniqueIdColumnName()).equals(
                rule.getColumnData(metaData.getUniqueIdColumnName()))) {
            throw new Exception("The rule to be deleted and the rule found are not the same."
                    + "Something went horribly wrong");
        }

        // Get rid of the leaf node
        stack.pop();

        // Handle the ancestors of the leaf
        while (!stack.isEmpty()) {
            Node node = stack.pop();

            // Visit nodes in leaf to root order and:
            // 1. If this is the only value in the popped node, delete the node.
            // 2. If there are other values too, remove this value from the node.
            if (node.getCount() <= 1) {
                node = null;
            } else {
                node.removeChildNode(rule.getColumnData(node.getName()));
            }
        }
    }
    @Override
    public List<Rule> getAllApplicableRules(Map<String, String> inputMap) throws Exception {
        if (inputMap != null) {
            Queue<Node> nodeQueue = new LinkedList<>();
            nodeQueue.add(root);

            for (RuleInputMetaData rimd : metaData.getInputColumnList()) {
                Queue<Node> nextQueue = new LinkedList<>();
                while (!nodeQueue.isEmpty()) {
                    Node node = nodeQueue.poll();
                    if (node.getName().equals(rimd.getName())) {
                        String value = inputMap.get(rimd.getName());
                        value = (value == null) ? "" : value;

                        List<Node> eligibleRules = node.getNodes(value, true);
                        if (eligibleRules != null && !eligibleRules.isEmpty()) {
                            nextQueue.addAll(eligibleRules);
                        }
                    }
                }
                nodeQueue = nextQueue;
            }

            if (!nodeQueue.isEmpty()) {
                List<Rule> rules = new ArrayList<>();
                for (Node node : nodeQueue) {
                    if (node.getRule() != null) {
                        rules.add(node.getRule());
                    }
                }

                return rules;
            }
        }

        return null;
    }

    private List<Rule> filterAllApplicableRules(List<Rule> applicableRules, Map<String, String> inputMap) throws Exception {
        List<Rule> remainingRules = new ArrayList<>();
        for (Rule applicableRule : applicableRules) {
            if (applicableRule.evaluate(inputMap)) {
                remainingRules.add(applicableRule);
            }
        }

        if (!remainingRules.isEmpty()) {
            Collections.sort(remainingRules, new RuleComparator());
        }

        return remainingRules;
    }

    /*
     * This class is used to sort lists of eligible rules to get the best fitting rule.
     * The sort also helps in determining the next applicable rule. It is not meant as
     * a general rule comparator as that does not make any sense at all (which is also why
     * the Rule class does not implement Comparable - it would suggest that, in general,
     * rules can be compared against each other for priority ordering or whatever).
     *
     * The comparator iterates over the input fields in decreasing order of priority and ranks
     * a specific value higher than 'Any'.
     */
    private class RuleComparator implements Comparator<Rule> {

        @Override
        public int compare(Rule rule1, Rule rule2) {
            for (RuleInputMetaData col : metaData.getInputColumnList()) {
                try {
                    String colName = col.getName();

                    if (colName.equals(metaData.getUniqueIdColumnName())
                            || colName.equals(metaData.getUniqueOutputColumnName())) {
                        continue;
                    }

                    RuleInput input1 = rule1.getColumnData(colName);
                    RuleInput input2 = rule2.getColumnData(colName);

                    /*
                     *  In going down the order of priority of inputs, the first mismatch will
                     *  yield the answer of the comparison. "" (meaning 'Any') matches everything,
                     *  but an exact match is better. So if the column values are unequal, whichever
                     *  rule has non-'Any' as the value will rank higher.
                     */
                    if (!input1.isConflicting(input2)) {
                        if (input1.isBetterFit(input2) != 0) {
                            return input1.isBetterFit(input2);
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.error("Error sorting rules", ex);
                }
            }

            // If all column values are same
            return 0;
        }
    }
}
