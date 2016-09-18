.. java:import:: com.github.kislayverma.rulette.core.data IDataProvider

.. java:import:: com.github.kislayverma.rulette.core.metadata RuleInputMetaData

.. java:import:: com.github.kislayverma.rulette.core.metadata RuleSystemMetaData

.. java:import:: com.github.kislayverma.rulette.core.rule Rule

.. java:import:: com.github.kislayverma.rulette.core.ruleinput.type RuleInputType

.. java:import:: com.github.kislayverma.rulette.mysql.dao DataSource

.. java:import:: java.io IOException

.. java:import:: java.sql Connection

.. java:import:: java.sql PreparedStatement

.. java:import:: java.sql ResultSet

.. java:import:: java.sql SQLException

.. java:import:: java.sql Statement

.. java:import:: java.util ArrayList

.. java:import:: java.util HashMap

.. java:import:: java.util List

.. java:import:: java.util Map

.. java:import:: java.util.concurrent ConcurrentHashMap

MysqlDataProvider
=================

.. java:package:: com.github.kislayverma.rulette.mysql
   :noindex:

.. java:type:: public class MysqlDataProvider implements IDataProvider

   :author: kislay.verma

Constructors
------------
MysqlDataProvider
^^^^^^^^^^^^^^^^^

.. java:constructor:: public MysqlDataProvider(String datasourceUrl) throws IOException, SQLException
   :outertype: MysqlDataProvider

Methods
-------
deleteRule
^^^^^^^^^^

.. java:method:: @Override public boolean deleteRule(String ruleSystemName, Rule rule) throws SQLException, Exception
   :outertype: MysqlDataProvider

getAllRules
^^^^^^^^^^^

.. java:method:: @Override public List<Rule> getAllRules(String ruleSystemName) throws SQLException, Exception
   :outertype: MysqlDataProvider

getRuleSystemMetaData
^^^^^^^^^^^^^^^^^^^^^

.. java:method:: @Override public RuleSystemMetaData getRuleSystemMetaData(String ruleSystemName) throws Exception
   :outertype: MysqlDataProvider

loadRuleSystemMetaData
^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public RuleSystemMetaData loadRuleSystemMetaData(String ruleSystemName) throws Exception
   :outertype: MysqlDataProvider

saveRule
^^^^^^^^

.. java:method:: @Override public Rule saveRule(String ruleSystemName, Rule rule) throws SQLException, Exception
   :outertype: MysqlDataProvider

updateRule
^^^^^^^^^^

.. java:method:: @Override public Rule updateRule(String ruleSystemName, Rule rule) throws SQLException, Exception
   :outertype: MysqlDataProvider

