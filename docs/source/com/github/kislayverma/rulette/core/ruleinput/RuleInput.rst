.. java:import:: com.github.kislayverma.rulette.core.metadata RuleInputMetaData

.. java:import:: com.github.kislayverma.rulette.core.ruleinput.type RuleInputType

.. java:import:: com.github.kislayverma.rulette.core.ruleinput.type ValueInput

.. java:import:: com.github.kislayverma.rulette.core.ruleinput.type RangeInput

.. java:import:: java.io Serializable

RuleInput
=========

.. java:package:: com.github.kislayverma.rulette.core.ruleinput
   :noindex:

.. java:type:: public abstract class RuleInput implements Serializable

Fields
------
metaData
^^^^^^^^

.. java:field:: protected RuleInputMetaData metaData
   :outertype: RuleInput

rawInput
^^^^^^^^

.. java:field:: protected String rawInput
   :outertype: RuleInput

Methods
-------
createRuleInput
^^^^^^^^^^^^^^^

.. java:method:: public static RuleInput createRuleInput(int id, String name, int priority, RuleInputType ruleType, String dataType, String value) throws Exception
   :outertype: RuleInput

evaluate
^^^^^^^^

.. java:method:: public abstract boolean evaluate(String value) throws Exception
   :outertype: RuleInput

getId
^^^^^

.. java:method:: public int getId()
   :outertype: RuleInput

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: RuleInput

getPriority
^^^^^^^^^^^

.. java:method:: public int getPriority()
   :outertype: RuleInput

getRawValue
^^^^^^^^^^^

.. java:method:: public final String getRawValue()
   :outertype: RuleInput

getRuleInputDataType
^^^^^^^^^^^^^^^^^^^^

.. java:method:: public String getRuleInputDataType()
   :outertype: RuleInput

getRuleInputType
^^^^^^^^^^^^^^^^

.. java:method:: public RuleInputType getRuleInputType()
   :outertype: RuleInput

isConflicting
^^^^^^^^^^^^^

.. java:method:: public abstract boolean isConflicting(RuleInput input) throws Exception
   :outertype: RuleInput

toString
^^^^^^^^

.. java:method:: @Override public String toString()
   :outertype: RuleInput

