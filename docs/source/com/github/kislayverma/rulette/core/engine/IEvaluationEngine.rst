.. java:import:: com.github.kislayverma.rulette.core.rule Rule

.. java:import:: java.util List

.. java:import:: java.util Map

IEvaluationEngine
=================

.. java:package:: com.github.kislayverma.rulette.core.engine
   :noindex:

.. java:type:: public interface IEvaluationEngine

   :author: kislay.verma

Methods
-------
addRule
^^^^^^^

.. java:method::  void addRule(Rule rule) throws Exception
   :outertype: IEvaluationEngine

deleteRule
^^^^^^^^^^

.. java:method::  void deleteRule(Rule rule) throws Exception
   :outertype: IEvaluationEngine

getAllApplicableRules
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  List<Rule> getAllApplicableRules(Map<String, String> inputMap) throws Exception
   :outertype: IEvaluationEngine

   This method returns all rules applicable for the given combination of rule inputs.

   :param inputMap: Map with input names as keys and their String values as values
   :throws java.lang.Exception: on rule evaluation error
   :return: null if input is null, null if no rule is applicable for the given input combination the applicable rule otherwise.

getAllRules
^^^^^^^^^^^

.. java:method::  List<Rule> getAllRules()
   :outertype: IEvaluationEngine

   This method returns a list of all the rules in the rule system.

   :return: List of all rules configured in the rule system

getNextApplicableRule
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  Rule getNextApplicableRule(Map<String, String> inputMap) throws Exception
   :outertype: IEvaluationEngine

   This method returns the next rule that will be applicable to the inputs if the current rule applicable to the were to be deleted.

   :param inputMap: Map with column Names as keys and column values as values.
   :throws java.lang.Exception: on rule evaluation error
   :return: A \ :java:ref:`Rule`\  object if a rule is applicable after the currently applicable rule is deleted. null if no rule is applicable after the currently applicable rule is deleted. null id no rule is currently applicable.

getRule
^^^^^^^

.. java:method::  Rule getRule(Integer ruleId)
   :outertype: IEvaluationEngine

   This method returns the applicable rule for the given input criteria.

   :param ruleId: Unique id of the rule to get looked up.
   :return: A \ :java:ref:`Rule`\  object if a rule with the given id exists. null otherwise.

getRule
^^^^^^^

.. java:method::  Rule getRule(Map<String, String> inputMap) throws Exception
   :outertype: IEvaluationEngine

   This method returns the rule applicable for the given combination of rule inputs.

   :param inputMap: Map with input names as keys and their String values as values
   :throws java.lang.Exception: on rule evaluation error
   :return: null if input is null, null if no rule is applicable for the given input combination the applicable rule otherwise.

