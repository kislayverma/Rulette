.. java:import:: com.github.kislayverma.rulette.core.engine IEvaluationEngine

.. java:import:: com.github.kislayverma.rulette.core.metadata RuleSystemMetaData

.. java:import:: com.github.kislayverma.rulette.core.rule Rule

.. java:import:: com.github.kislayverma.rulette.core.metadata RuleInputMetaData

.. java:import:: com.github.kislayverma.rulette.core.ruleinput.type RuleInputType

.. java:import:: com.github.kislayverma.rulette.engine.impl.trie.node Node

.. java:import:: com.github.kislayverma.rulette.engine.impl.trie.node RangeNode

.. java:import:: com.github.kislayverma.rulette.engine.impl.trie.node ValueNode

.. java:import:: java.util ArrayList

.. java:import:: java.util Collections

.. java:import:: java.util Comparator

.. java:import:: java.util LinkedList

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util Queue

.. java:import:: java.util Stack

.. java:import:: java.util.concurrent ConcurrentHashMap

TrieBasedEvaluationEngine
=========================

.. java:package:: com.github.kislayverma.rulette.engine.impl.trie
   :noindex:

.. java:type:: public class TrieBasedEvaluationEngine implements IEvaluationEngine

   :author: kislay.verma

Constructors
------------
TrieBasedEvaluationEngine
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public TrieBasedEvaluationEngine(RuleSystemMetaData metaData) throws Exception
   :outertype: TrieBasedEvaluationEngine

TrieBasedEvaluationEngine
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:constructor:: public TrieBasedEvaluationEngine(RuleSystemMetaData metaData, List<Rule> rules) throws Exception
   :outertype: TrieBasedEvaluationEngine

Methods
-------
addRule
^^^^^^^

.. java:method:: @Override public void addRule(Rule rule) throws Exception
   :outertype: TrieBasedEvaluationEngine

deleteRule
^^^^^^^^^^

.. java:method:: @Override public void deleteRule(Rule rule) throws Exception
   :outertype: TrieBasedEvaluationEngine

getAllApplicableRules
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public List<Rule> getAllApplicableRules(Map<String, String> inputMap) throws Exception
   :outertype: TrieBasedEvaluationEngine

getAllRules
^^^^^^^^^^^

.. java:method:: @Override public List<Rule> getAllRules()
   :outertype: TrieBasedEvaluationEngine

getNextApplicableRule
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public Rule getNextApplicableRule(Map<String, String> inputMap) throws Exception
   :outertype: TrieBasedEvaluationEngine

getRule
^^^^^^^

.. java:method:: @Override public Rule getRule(Integer ruleId)
   :outertype: TrieBasedEvaluationEngine

getRule
^^^^^^^

.. java:method:: @Override public Rule getRule(Map<String, String> inputMap) throws Exception
   :outertype: TrieBasedEvaluationEngine

