.. java:import:: com.github.kislayverma.rulette.core.data IDataProvider

.. java:import:: com.github.kislayverma.rulette.core.engine IEvaluationEngine

.. java:import:: com.github.kislayverma.rulette.core.metadata RuleSystemMetaData

.. java:import:: com.github.kislayverma.rulette.core.rule Rule

.. java:import:: com.github.kislayverma.rulette.core.ruleinput RuleInputConfigurator

.. java:import:: com.github.kislayverma.rulette.core.metadata RuleInputMetaData

.. java:import:: java.io Serializable

.. java:import:: java.util ArrayList

.. java:import:: java.util Date

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: com.github.kislayverma.rulette.core.util RuletteInputProcessor

.. java:import:: com.github.kislayverma.rulette.engine.impl.trie TrieBasedEvaluationEngine

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

RuleSystem
==========

.. java:package:: com.github.kislayverma.rulette
   :noindex:

.. java:type:: public class RuleSystem implements Serializable

   This class models a rule-system comprising of rules and provides appropriate APIs to interact with it. \ **Sample usage**\

   .. parsed-literal::

      RuleSystem rs = new RuleSystem("rule system name");
      Rule r = rs.getRule(25L);

   :author: Kislay Verma

Constructors
------------
RuleSystem
^^^^^^^^^^

.. java:constructor:: public RuleSystem(String ruleSystemName, IDataProvider dataProvider) throws Exception
   :outertype: RuleSystem

   This constructor initializes a rule system of the given name by reading data from the credentials given in the data source URL. All rule input will be initiatized with default parameters and no custom data types will be supported.

   :param ruleSystemName: Name of the rule system to be instantiated
   :param dataProvider:
   :throws Exception: on rule system initialization failure

RuleSystem
^^^^^^^^^^

.. java:constructor:: public RuleSystem(String ruleSystemName, IDataProvider dataProvider, RuleInputConfigurator inputConfig) throws Exception
   :outertype: RuleSystem

   This constructor initializes a rule system of the given name by reading data from the credentials given in the data source URL. Rule input will be initiatized with default parameters unless an override is provided via the inputConfig parameter. Custom data type will be supported only if appropriate configuration is provided.

   :param ruleSystemName: Name of the rule system to be instantiated
   :param dataProvider:
   :param inputConfig: Configuration to support custom data types and behaviour for rule inputs
   :throws Exception: if rule system could not be initialized

Methods
-------
addRule
^^^^^^^

.. java:method:: public Rule addRule(Map<String, String> inputMap) throws Exception
   :outertype: RuleSystem

   This method adds a new rule to the rule system. There is no need to provide the rule_id field in the input - it will be auto-populated.

   :param inputMap: The rule input values for which a new rule is to be added
   :throws Exception: on failure
   :return: the added rule if there are no overlapping rules null if there are overlapping rules null if the input constitutes an invalid rule as per the validation policy in use.

addRule
^^^^^^^

.. java:method:: public Rule addRule(Rule newRule) throws Exception
   :outertype: RuleSystem

   This method adds the given rule to the rule system with a new rule id.

   :param newRule: The new rule to be added
   :throws Exception: on failure
   :return: the added rule if there are no overlapping rules null if there are overlapping rules null if the input constitutes an invalid rule as per the validation policy in use.

createRuleObject
^^^^^^^^^^^^^^^^

.. java:method:: public Rule createRuleObject(Map<String, String> inputMap) throws Exception
   :outertype: RuleSystem

deleteRule
^^^^^^^^^^

.. java:method:: public boolean deleteRule(Integer ruleId) throws Exception
   :outertype: RuleSystem

   This method deletes an existing rule from the rule system.

   :param ruleId: Unique id of the rule to be deleted
   :throws Exception: on error in deleting rule
   :return: true if the rule with given rule id was successfully deleted false if the given rule does not exist false if the given rule could not be deleted (for whatever reason).

deleteRule
^^^^^^^^^^

.. java:method:: public boolean deleteRule(Rule rule) throws Exception
   :outertype: RuleSystem

   This method deleted the given rule from the rule system.

   :param rule: The \ :java:ref:`Rule`\  to be deleted.
   :throws Exception: on failure
   :return: true if the given rule was successfully deleted false if the given rule does not exist false if the given rule could not be deleted (for whatever reason).

getAllApplicableRules
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public List<Rule> getAllApplicableRules(Map<String, String> inputMap) throws Exception
   :outertype: RuleSystem

   This method returns a list of all the rules in the rule system.

   :param inputMap: map of rule input values for which applicable rules are to be returned
   :throws java.lang.Exception: if rule evaluation fails
   :return: List of all rules applicable to the given input

getAllColumnNames
^^^^^^^^^^^^^^^^^

.. java:method:: public List<String> getAllColumnNames()
   :outertype: RuleSystem

   Returns the names of all the columns in the rule system, including the unique input and output column names

   :return: names of all the columns in the rule system

getAllRules
^^^^^^^^^^^

.. java:method:: public List<Rule> getAllRules()
   :outertype: RuleSystem

   This method returns a list of all the rules in the rule system.

   :return: List of all rules configured in the rule system

getConflictingRules
^^^^^^^^^^^^^^^^^^^

.. java:method:: public List<Rule> getConflictingRules(Rule rule) throws Exception
   :outertype: RuleSystem

   This method returns a list of rules conflicting with the given rule.

   :param rule: \ :java:ref:`Rule`\  object
   :throws Exception: on rule evaluation failure
   :return: List of conflicting rules if any, empty list otherwise.

getInputColumnNames
^^^^^^^^^^^^^^^^^^^

.. java:method:: public List<String> getInputColumnNames()
   :outertype: RuleSystem

   Returns the names of all the columns in the rule system, excluding the unique input and output column names

   :return: names of all columns for evaluation in the rule system

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: RuleSystem

   Returns the name of the rule system

   :return: the name of the rule system

getNextApplicableRule
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public Rule getNextApplicableRule(Map<String, String> inputMap) throws Exception
   :outertype: RuleSystem

   This method returns the next rule that will be applicable to the inputs if the current rule applicable to the were to be deleted.

   :param inputMap: Map with column Names as keys and column values as values.
   :throws java.lang.Exception: on rule evaluation failure
   :return: A \ :java:ref:`Rule`\  object if a rule is applicable after the currently applicable rule is deleted. null if no rule is applicable after the currently applicable rule is deleted. null id no rule is currently applicable.

getOutputColumnName
^^^^^^^^^^^^^^^^^^^

.. java:method:: public String getOutputColumnName()
   :outertype: RuleSystem

getRule
^^^^^^^

.. java:method:: public Rule getRule(Object request) throws Exception
   :outertype: RuleSystem

   This method returns the rule applicable for the given combination of rule inputs.

   :param request: A simple Object with @RuletteInput annotation on required fields values
   :throws java.lang.Exception: on rule evaluation error
   :return: null if input is null, null if no rule is applicable for the given input combination the applicable rule otherwise.

getRule
^^^^^^^

.. java:method:: public Rule getRule(Map<String, String> inputMap) throws Exception
   :outertype: RuleSystem

   This method returns the rule applicable for the given combination of rule inputs.

   :param inputMap: Map with input names as keys and their String values as values
   :throws java.lang.Exception: on rule evaluation error
   :return: null if input is null, null if no rule is applicable for the given input combination the applicable rule otherwise.

getRule
^^^^^^^

.. java:method:: public Rule getRule(Integer ruleId)
   :outertype: RuleSystem

   This method returns the applicable rule for the given input criteria.

   :param ruleId: Unique id of the rule to get looked up.
   :return: A \ :java:ref:`Rule`\  object if a rule with the given id exists. null otherwise.

getUniqueColumnName
^^^^^^^^^^^^^^^^^^^

.. java:method:: public String getUniqueColumnName()
   :outertype: RuleSystem

updateRule
^^^^^^^^^^

.. java:method:: public Rule updateRule(Rule oldRule, Rule newRule) throws Exception
   :outertype: RuleSystem

   This method updates an existing rules with values of the new rule given. All fields are updated of the old rule are updated. The new rule is checked for conflicts before update.

   :param oldRule: An existing rule
   :param newRule: The rule containing the new field values to which the old rule will be updated.
   :throws Exception: if there are overlapping rules if the old rules does not actually exist.
   :return: the updated rule if update creates no conflict. null if the input constitutes an invalid rule as per the validation policy in use.

