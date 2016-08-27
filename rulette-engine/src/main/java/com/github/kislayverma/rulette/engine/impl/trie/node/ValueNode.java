package com.github.kislayverma.rulette.engine.impl.trie.node;

import com.github.kislayverma.rulette.core.ruleinput.RuleInput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ValueNode extends Node implements Serializable {

    private final Map<String, Node> fieldMap = new ConcurrentHashMap<>();

    public ValueNode(String fieldName) {
        super(fieldName);
    }

    @Override
    public void addChildNode(RuleInput ruleInput, Node childNode) {
        this.fieldMap.put(ruleInput.getRawValue(), childNode);
    }

    @Override
    public void removeChildNode(RuleInput ruleInput) {
        this.fieldMap.remove(ruleInput.getRawValue());
    }

    @Override
    public List<Node> getNodes(String value, boolean getAnyValue) {
        List<Node> nodeList = new ArrayList<>();
        if (getAnyValue && (value == null || value.equals(""))) {
            nodeList.addAll(this.fieldMap.values());
        } else {
            Node node = this.fieldMap.get(value);
            if (node != null) {
                nodeList.add(node);
            }

            // If 'any' matches are also requested, and the rule input isn't
            // already an 'Any' value.
            if (getAnyValue && !"".equals(value)) {
                node = this.fieldMap.get("");
                if (node != null) {
                    nodeList.add(node);
                }
            }
        }

        return nodeList;
    }

    @Override
    public List<Node> getNodesForAddingRule(String value) {
        return getNodes(value, false);
    }

    @Override
    public int getCount() {
        return this.fieldMap.size();
    }

    @Override
    public Node getMatchingRule(String value) {
        if (value == null) {
            return null;
        }
        return this.fieldMap.get(value);
    }
}
