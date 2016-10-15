package com.github.kislayverma.rulette.engine.impl.trie.node;

import com.github.kislayverma.rulette.core.ruleinput.RuleInput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RangeNode extends Node implements Serializable {
    private static final long serialVersionUID = 8644727351374435060L;

    private final Map<RuleInput, Node> fieldMap = new ConcurrentHashMap<>();

    public RangeNode(String fieldName) {
        super(fieldName);
    }

    @Override
    public void addChildNode(RuleInput ruleInput, Node childNode) {
        this.fieldMap.put(ruleInput, childNode);
    }

    @Override
    public void removeChildNode(RuleInput ruleInput) {
        this.fieldMap.remove(ruleInput);
    }

    @Override
    public List<Node> getNodes(String value, boolean getAnyValue) throws Exception {
        List<Node> nodeList = new ArrayList<>();
        if (value == null || value.equals("")) {
            nodeList.addAll(this.fieldMap.values());
        } else {
            for (Map.Entry<RuleInput, Node> entry : this.fieldMap.entrySet()) {
                if (entry.getKey().isAny() && !getAnyValue) {
                    continue;
                }

                if (entry.getKey().evaluate(value)) {
                    nodeList.add(entry.getValue());
                }
            }
        }

        return nodeList;
    }

    @Override
    public List<Node> getNodesForAddingRule(RuleInput ruleInput) throws Exception {
        List<Node> nodeList = new ArrayList<>();
        for (Map.Entry<RuleInput, Node> entry : this.fieldMap.entrySet()) {
            if (entry.getKey().equals(ruleInput)) {
                nodeList.add(entry.getValue());
            }
        }

        return nodeList;
    }

    @Override
    public int getCount() {
        return this.fieldMap.entrySet().size();
    }

    @Override
    public Node getMatchingRule(String value) {
        for (Map.Entry<RuleInput, Node> entry : this.fieldMap.entrySet()) {
            if (value.equals(entry.getKey().getRawValue())) {
                return entry.getValue();
            }
        }

        return null;
    }
}
