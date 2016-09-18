.. java:import:: com.github.kislayverma.rulette.core.ruleinput RuleInput

.. java:import:: java.io Serializable

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util.concurrent ConcurrentHashMap

ValueNode
=========

.. java:package:: com.github.kislayverma.rulette.engine.impl.trie.node
   :noindex:

.. java:type:: public class ValueNode extends Node implements Serializable

Constructors
------------
ValueNode
^^^^^^^^^

.. java:constructor:: public ValueNode(String fieldName)
   :outertype: ValueNode

Methods
-------
addChildNode
^^^^^^^^^^^^

.. java:method:: @Override public void addChildNode(RuleInput ruleInput, Node childNode)
   :outertype: ValueNode

getCount
^^^^^^^^

.. java:method:: @Override public int getCount()
   :outertype: ValueNode

getMatchingRule
^^^^^^^^^^^^^^^

.. java:method:: @Override public Node getMatchingRule(String value)
   :outertype: ValueNode

getNodes
^^^^^^^^

.. java:method:: @Override public List<Node> getNodes(String value, boolean getAnyValue)
   :outertype: ValueNode

getNodesForAddingRule
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Node> getNodesForAddingRule(String value)
   :outertype: ValueNode

removeChildNode
^^^^^^^^^^^^^^^

.. java:method:: @Override public void removeChildNode(RuleInput ruleInput)
   :outertype: ValueNode

