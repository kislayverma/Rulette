.. java:import:: com.github.kislayverma.rulette.core.metadata RuleSystemMetaData

.. java:import:: com.github.kislayverma.rulette.core.ruleinput RuleInput

.. java:import:: com.github.kislayverma.rulette.core.metadata RuleInputMetaData

.. java:import:: com.github.kislayverma.rulette.core.ruleinput.type RuleInputType

.. java:import:: com.github.kislayverma.rulette.core.ruleinput.value DefaultDataType

.. java:import:: java.io Serializable

.. java:import:: java.util HashMap

.. java:import:: java.util Map

Rule
====

.. java:package:: com.github.kislayverma.rulette.core.rule
   :noindex:

.. java:type:: public class Rule implements Serializable

   This class models a rule in the rule system. It has input columns and an output value which the rule system maps these inputs to.

   :author: Kislay

Constructors
------------
Rule
^^^^

.. java:constructor:: public Rule(RuleSystemMetaData ruleSystemMetaData, Map<String, String> inputMap) throws Exception
   :outertype: Rule

   This constructor takes the list of columns in the rule system and a map of value to populate the fields of this rule. Any fields missing in the input are set to blank (meaning 'Any').

   :param ruleSystemMetaData:
   :param inputMap: input values for constructing the rule
   :throws Exception: on rule construction error

Methods
-------
evaluate
^^^^^^^^

.. java:method:: public boolean evaluate(Map<String, String> inputMap) throws Exception
   :outertype: Rule

   This method accepts a column name to column value mapping and return if the mapping is true for this rule. i.e. It returns true if this rule is applicable for the input values and false otherwise. The method returns true if one the following criteria are met for each column:

   ..

   #. Both rule and input are equal (same value or both being 'Any')
   #. Rule is "any"

   In all other cases false is returned.

   :param inputMap: rule input values for evaluation
   :throws java.lang.Exception: on rule evaluation error
   :return: true if input values match this rule

getColumnData
^^^^^^^^^^^^^

.. java:method:: public RuleInput getColumnData(String colName)
   :outertype: Rule

isConflicting
^^^^^^^^^^^^^

.. java:method:: public boolean isConflicting(Rule rule) throws Exception
   :outertype: Rule

   Returns true if the give rule conflicts with this rule.

   :param rule: input rule to be checked for conflict
   :throws Exception: on evaluation error
   :return: true if input rule conflicts with this rule, false otherwise

setColumnData
^^^^^^^^^^^^^

.. java:method:: public Rule setColumnData(String colName, String value) throws Exception
   :outertype: Rule

   This method is used to modify the values of rule inputs in a rule. To prevent someone from accidentally modifying column values which propagate throughout the system, this method creates a copy of the current rule, overwrites the specified column with the given value, and returns a new rule. This keeps rule objects unmodifiable to a reasonable extent.

   :param colName: column name whose value is to be set
   :param value: the value to be set
   :throws Exception: if new rule construction fails
   :return: New rule object with the new value set

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: Rule

