.. java:import:: com.github.kislayverma.rulette.core.ruleinput RuleInput

.. java:import:: com.github.kislayverma.rulette.core.metadata RuleInputMetaData

.. java:import:: com.github.kislayverma.rulette.core.ruleinput RuleInputValueFactory

.. java:import:: com.github.kislayverma.rulette.core.ruleinput.value IInputValue

.. java:import:: java.io Serializable

ValueInput
==========

.. java:package:: com.github.kislayverma.rulette.core.ruleinput.type
   :noindex:

.. java:type:: public class ValueInput extends RuleInput implements Serializable

Constructors
------------
ValueInput
^^^^^^^^^^

.. java:constructor:: public ValueInput(int id, String name, int priority, String inputDataType, String value) throws Exception
   :outertype: ValueInput

Methods
-------
evaluate
^^^^^^^^

.. java:method:: @Override public boolean evaluate(String value) throws Exception
   :outertype: ValueInput

isConflicting
^^^^^^^^^^^^^

.. java:method:: @Override public boolean isConflicting(RuleInput input) throws Exception
   :outertype: ValueInput

   The given input conflicts with this if the values are same.

   :param input: input to be checked for conflict
   :throws Exception: on failure of conflict evaluation
   :return: true is this rule input conflicts with the one passed in, true otherwise

