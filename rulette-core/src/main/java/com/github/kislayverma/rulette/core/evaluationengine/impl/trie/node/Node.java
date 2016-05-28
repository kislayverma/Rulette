package com.github.kislayverma.rulette.core.evaluationengine.impl.trie.node;

import com.github.kislayverma.rulette.core.rule.Rule;
import com.github.kislayverma.rulette.core.ruleinput.RuleInput;
import java.util.List;

public abstract class Node {
	// This will be populated if this is a leaf node
	protected Rule rule;

	// Represents the field name modelled by this node
	protected String name;

	public abstract void addChildNode(RuleInput ruleInput, Node childNode);

	public abstract void removeChildNode(RuleInput ruleInput);

	public abstract List<Node> getNodes(String value, boolean getAnyValue) throws Exception;

	public abstract List<Node> getNodesForAddingRule(String value) throws Exception;

    public abstract int getCount();

	/**
	 * This method is specifically added to enable traversal of the rule system trie to 
	 * find a specific rule. It takes the exact values of rule input fields (value or range) 
	 * and literally matches them against the keys of the trie. This allows us to locate a 
	 * specific rule in the trie.
     * @param value value to be matched
     * @return matching node if one exists, null otherwise
	 */
	public abstract Node getMatchingRule(String value);

	Node (String fieldName) {
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
