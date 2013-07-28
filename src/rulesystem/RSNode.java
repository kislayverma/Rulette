package rulesystem;

import java.util.List;

import rulesystem.ruleinput.RuleInput;

abstract class RSNode {
	protected Rule rule;

	public abstract void addChildNode(RuleInput ruleInput, RSNode childNode);

	public abstract List<RSNode> getNodes(String value, boolean getAnyValue);

	public Rule getRule() {
		return this.rule;
	}

	public abstract int getCount();
	
	public void setRule(Rule rule) {
		this.rule = rule;
	}
}
