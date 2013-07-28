package rulesystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rulesystem.ruleinput.RuleInput;

class ValueRSNode extends RSNode {
	private Map<String, RSNode> fieldMap = new HashMap<>();

	@Override
	public void addChildNode(RuleInput ruleInput, RSNode childNode) {
		this.fieldMap.put(ruleInput.getValue(), childNode);
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
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, RSNode> entry : this.fieldMap.entrySet()) {
			sb.append(entry.getKey()).append(", ");
		}
		
		return sb.toString();
	}
}
