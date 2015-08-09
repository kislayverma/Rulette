package rulesystem.rule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import rulesystem.ruleinput.RuleInput;

public class RangeRSNode extends RSNode implements Serializable {

    private Map<RuleInput, RSNode> fieldMap = new ConcurrentHashMap<>();

    public RangeRSNode(String fieldName) {
        super(fieldName);
    }

    @Override
    public void addChildNode(RuleInput ruleInput, RSNode childNode) {
        this.fieldMap.put(ruleInput, childNode);
    }

    @Override
    public void removeChildNode(RuleInput ruleInput) {
        this.fieldMap.remove(ruleInput);
    }

    @Override
    public List<RSNode> getNodes(String value, boolean getAnyValue) throws Exception {
        List<RSNode> nodeList = new ArrayList<>();
        for (Map.Entry<RuleInput, RSNode> entry : this.fieldMap.entrySet()) {
            if ("".equals(entry.getKey().getRawValue()) && !getAnyValue) {
                continue;
            }

            if (entry.getKey().evaluate(value)) {
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
    public RSNode getMatchingRule(String value) {
        for (Map.Entry<RuleInput, RSNode> entry : this.fieldMap.entrySet()) {
            if (value.equals(entry.getKey().getRawValue())) {
                return entry.getValue();
            }
        }

        return null;
    }
}
