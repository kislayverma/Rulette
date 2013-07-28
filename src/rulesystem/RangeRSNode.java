package rulesystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rulesystem.ruleinput.RuleInput;

class RangeRSNode extends RSNode {
	private Map<RuleInput, RSNode> fieldMap = new HashMap<>();

	@Override
	public void addChildNode(RuleInput ruleInput, RSNode childNode) {
		this.fieldMap.put(ruleInput, childNode);
	}

	@Override
	public List<RSNode> getNodes(String value, boolean getAnyValue) {
		List<RSNode> nodeList = new ArrayList<>();
		for (Map.Entry<RuleInput, RSNode> entry : this.fieldMap.entrySet()) {
			if ("".equals(entry.getKey().getValue()) && !getAnyValue) {
				continue;
			}

			if (entry.getKey().evaluate(value)) {
				nodeList.add(entry.getValue());
			}
		}

		return nodeList;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<RuleInput, RSNode> entry : this.fieldMap.entrySet()) {
			sb.append(entry.getKey().getValue()).append(", ");
		}

		return sb.toString();
	}

	@Override
	public int getCount() {
		return this.fieldMap.entrySet().size();
	}
}
