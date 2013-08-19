package rulesystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rulesystem.ruleinput.RuleInput;

class ValueRSNode extends RSNode {
	private Map<String, RSNode> fieldMap = new ConcurrentHashMap<>();

	ValueRSNode(String fieldName) {
		super(fieldName);
	}

	@Override
	public void addChildNode(RuleInput ruleInput, RSNode childNode) {
		this.fieldMap.put(ruleInput.getValue(), childNode);
	}

	@Override
	public void removeChildNode(RuleInput ruleInput) {
		this.fieldMap.remove(ruleInput.getValue());
	}

	@Override
	public List<RSNode> getNodes(String value, boolean getAnyValue) {
		List<RSNode> nodeList = new ArrayList<>();
		RSNode node = this.fieldMap.get(value);
		if (node != null) {
			nodeList.add(node);
		}

		// If 'any' matches are also requested, and the rule input isn't 
		// already an 'Any' value.
		if (getAnyValue && ! "".equals(value)) {
    		node = this.fieldMap.get("");
    		if (node != null) {
    			nodeList.add(node);
    		}
		}

		return nodeList;
	}

	@Override
	public int getCount() {
		return this.fieldMap.size();
	}

	@Override
	public RSNode getMatchingRule(String value) {
		if (value == null) {
			return null;
		}
		return this.fieldMap.get(value);
	}
}
