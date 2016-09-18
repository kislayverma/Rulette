.. java:import:: com.github.kislayverma.rulette.core.ruleinput.type RuleInputType

.. java:import:: java.io Serializable

RuleInputMetaData
=================

.. java:package:: com.github.kislayverma.rulette.core.metadata
   :noindex:

.. java:type:: public class RuleInputMetaData implements Serializable

Constructors
------------
RuleInputMetaData
^^^^^^^^^^^^^^^^^

.. java:constructor:: public RuleInputMetaData(int id, String name, int priority, RuleInputType ruleType, String dataType) throws Exception
   :outertype: RuleInputMetaData

Methods
-------
getDataType
^^^^^^^^^^^

.. java:method:: public String getDataType()
   :outertype: RuleInputMetaData

getId
^^^^^

.. java:method:: public int getId()
   :outertype: RuleInputMetaData

getName
^^^^^^^

.. java:method:: public String getName()
   :outertype: RuleInputMetaData

getPriority
^^^^^^^^^^^

.. java:method:: public int getPriority()
   :outertype: RuleInputMetaData

getRuleInputType
^^^^^^^^^^^^^^^^

.. java:method:: public RuleInputType getRuleInputType()
   :outertype: RuleInputMetaData

