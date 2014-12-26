package rulesystem;

import java.util.List;

import rulesystem.ruleinput.RuleInput;

abstract class RSNode {
	// This will be populated if this is a leaf node
	protected Rule rule;

	// Represents the field name modelled by this node
	protected String name;

	public abstract void addChildNode(RuleInput ruleInput, RSNode childNode);

	public abstract void removeChildNode(RuleInput ruleInput);

	public abstract List<RSNode> getNodes(String value, boolean getAnyValue) throws Exception;

	public abstract int getCount();

	/**
	 * This method is specifically added to enable traversal of the rule system trie to 
	 * find a specific rule. It takes the exact values of rule input fields (value or range) 
	 * and literally matches them against the keys of the trie. This allows us to locate a 
	 * specific rule in the trie.
	 */
	public abstract RSNode getMatchingRule(String value);

	RSNode (String fieldName) {
		this.name = fieldName;
	}

	public Rule getRule() {
		return this.rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public String getName() {
		return this.name;
	}
}
