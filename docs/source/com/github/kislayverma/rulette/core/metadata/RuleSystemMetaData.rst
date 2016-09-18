.. java:import:: com.github.kislayverma.rulette.core.ruleinput RuleInputConfiguration

.. java:import:: com.github.kislayverma.rulette.core.ruleinput RuleInputConfigurator

.. java:import:: com.github.kislayverma.rulette.core.ruleinput RuleInputValueFactory

.. java:import:: com.github.kislayverma.rulette.core.ruleinput.value DefaultDataType

.. java:import:: com.github.kislayverma.rulette.core.ruleinput.value.defaults DefaultBuilderRegistry

.. java:import:: java.util List

RuleSystemMetaData
==================

.. java:package:: com.github.kislayverma.rulette.core.metadata
   :noindex:

.. java:type:: public class RuleSystemMetaData

Constructors
------------
RuleSystemMetaData
^^^^^^^^^^^^^^^^^^

.. java:constructor:: public RuleSystemMetaData(String ruleSystemName, String tableName, String uniqueIdColName, String uniqueOutputColName, List<RuleInputMetaData> inputs) throws Exception
   :outertype: RuleSystemMetaData

Methods
-------
applyCustomConfiguration
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void applyCustomConfiguration(RuleInputConfigurator configuration) throws Exception
   :outertype: RuleSystemMetaData

   This method loads default configuration for all rule inputs if no custom override is given (in which case it overrides the defaults). Input and output columns always get default configuration.

   :param configuration: Custom configuration for rule inputs
   :throws Exception: on failure to register rule input configuration

getInputColumnList
^^^^^^^^^^^^^^^^^^

.. java:method:: public List<RuleInputMetaData> getInputColumnList()
   :outertype: RuleSystemMetaData

getRuleSystemName
^^^^^^^^^^^^^^^^^

.. java:method:: public String getRuleSystemName()
   :outertype: RuleSystemMetaData

getTableName
^^^^^^^^^^^^

.. java:method:: public String getTableName()
   :outertype: RuleSystemMetaData

getUniqueIdColumnName
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public String getUniqueIdColumnName()
   :outertype: RuleSystemMetaData

getUniqueOutputColumnName
^^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public String getUniqueOutputColumnName()
   :outertype: RuleSystemMetaData

