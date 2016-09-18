.. java:import:: com.github.kislayverma.rulette.core.ruleinput RuleInput

.. java:import:: java.io Serializable

.. java:import:: java.util ArrayList

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util.concurrent ConcurrentHashMap

RangeNode
=========

.. java:package:: com.github.kislayverma.rulette.engine.impl.trie.node
   :noindex:

.. java:type:: public class RangeNode extends Node implements Serializable

Constructors
------------
RangeNode
^^^^^^^^^

.. java:constructor:: public RangeNode(String fieldName)
   :outertype: RangeNode

Methods
-------
addChildNode
^^^^^^^^^^^^

.. java:method:: @Override public void addChildNode(RuleInput ruleInput, Node childNode)
   :outertype: RangeNode

getCount
^^^^^^^^

.. java:method:: @Override public int getCount()
   :outertype: RangeNode

getMatchingRule
^^^^^^^^^^^^^^^

.. java:method:: @Override public Node getMatchingRule(String value)
   :outertype: RangeNode

getNodes
^^^^^^^^

.. java:method:: @Override public List<Node> getNodes(String value, boolean getAnyValue) throws Exception
   :outertype: RangeNode

getNodesForAddingRule
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Node> getNodesForAddingRule(String value) throws Exception
   :outertype: RangeNode

removeChildNode
^^^^^^^^^^^^^^^

.. java:method:: @Override public void removeChildNode(RuleInput ruleInput)
   :outertype: RangeNode

