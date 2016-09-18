.. java:import:: com.github.kislayverma.rulette.core.ruleinput RuleInput

.. java:import:: com.github.kislayverma.rulette.core.metadata RuleInputMetaData

.. java:import:: com.github.kislayverma.rulette.core.ruleinput RuleInputValueFactory

.. java:import:: com.github.kislayverma.rulette.core.ruleinput.value IInputValue

.. java:import:: java.io Serializable

RangeInput
==========

.. java:package:: com.github.kislayverma.rulette.core.ruleinput.type
   :noindex:

.. java:type:: public class RangeInput extends RuleInput implements Serializable

Constructors
------------
RangeInput
^^^^^^^^^^

.. java:constructor:: public RangeInput(int id, String name, int priority, String inputDataType, String value) throws Exception
   :outertype: RangeInput

Methods
-------
evaluate
^^^^^^^^

.. java:method:: @Override public boolean evaluate(String value) throws Exception
   :outertype: RangeInput

isConflicting
^^^^^^^^^^^^^

.. java:method:: @Override public boolean isConflicting(RuleInput input) throws Exception
   :outertype: RangeInput

   The input rule input conflicts with this if the ranges specified by the two are overlapping.

   :param input: input to be checked for conflict
   :throws Exception: on failure of conflict evaluation
   :return: true is this rule input conflicts with the one passed in, true otherwise

