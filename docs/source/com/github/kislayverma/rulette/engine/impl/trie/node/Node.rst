.. java:import:: com.github.kislayverma.rulette.core.rule Rule

.. java:import:: com.github.kislayverma.rulette.core.ruleinput RuleInput

.. java:import:: java.util List

Node
====

.. java:package:: com.github.kislayverma.rulette.engine.impl.trie.node
   :noindex:

.. java:type:: public abstract class Node

Fields
------
name
^^^^

.. java:field:: protected String name
   :outertype: Node

rule
^^^^

.. java:field:: protected Rule rule
   :outertype: Node

Constructors
------------
Node
^^^^

.. java:constructor::  Node(String fieldName)
   :outertype: Node

Methods
-------
addChildNode
^^^^^^^^^^^^

.. java:method:: public abstract void addChildNode(RuleInput ruleInput, Node childNode)
   :outertype: Node

getCount
^^^^^^^^

.. java:method:: public abstract int getCount()
   :outertype: Node

getMatchingRule
^^^^^^^^^^^^^^^

.. java:method:: public abstract Node getMatchingRule(String value)
   :outertype: Node

   This method is specifically added to enable traversal of the rule system trie to find a specific rule. It takes the exact values of rule input fields (value or range) and literally matches them against the keys of the trie. This allows us to locate a specific rule in the trie.

   :param value: value to be matched
   :return: matching node if one exists, null otherwise

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: Node

getNodes
^^^^^^^^

.. java:method:: public abstract List<Node> getNodes(String value, boolean getAnyValue) throws Exception
   :outertype: Node

getNodesForAddingRule
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public abstract List<Node> getNodesForAddingRule(String value) throws Exception
   :outertype: Node

getRule
^^^^^^^

.. java:method:: public Rule getRule()
   :outertype: Node

removeChildNode
^^^^^^^^^^^^^^^

.. java:method:: public abstract void removeChildNode(RuleInput ruleInput)
   :outertype: Node

setRule
^^^^^^^

.. java:method:: public void setRule(Rule rule)
   :outertype: Node

