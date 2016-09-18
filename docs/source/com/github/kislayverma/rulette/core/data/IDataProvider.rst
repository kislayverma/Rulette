.. java:import:: com.github.kislayverma.rulette.core.metadata RuleSystemMetaData

.. java:import:: com.github.kislayverma.rulette.core.rule Rule

.. java:import:: java.sql SQLException

.. java:import:: java.util List

IDataProvider
=============

.. java:package:: com.github.kislayverma.rulette.core.data
   :noindex:

.. java:type:: public interface IDataProvider

   :author: kislay.verma

Methods
-------
deleteRule
^^^^^^^^^^

.. java:method::  boolean deleteRule(String ruleSystemName, Rule rule) throws SQLException, Exception
   :outertype: IDataProvider

getAllRules
^^^^^^^^^^^

.. java:method::  List<Rule> getAllRules(String ruleSystemName) throws SQLException, Exception
   :outertype: IDataProvider

getRuleSystemMetaData
^^^^^^^^^^^^^^^^^^^^^

.. java:method::  RuleSystemMetaData getRuleSystemMetaData(String ruleSystemName) throws Exception
   :outertype: IDataProvider

saveRule
^^^^^^^^

.. java:method::  Rule saveRule(String ruleSystemName, Rule rule) throws SQLException, Exception
   :outertype: IDataProvider

updateRule
^^^^^^^^^^

.. java:method::  Rule updateRule(String ruleSystemName, Rule rule) throws SQLException, Exception
   :outertype: IDataProvider

