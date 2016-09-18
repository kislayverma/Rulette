.. java:import:: com.mchange.v2.c3p0 ComboPooledDataSource

.. java:import:: java.beans PropertyVetoException

.. java:import:: java.io IOException

.. java:import:: java.sql Connection

.. java:import:: java.sql SQLException

.. java:import:: java.sql Statement

.. java:import:: java.util Properties

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

DataSource
==========

.. java:package:: com.github.kislayverma.rulette.mysql.dao
   :noindex:

.. java:type:: public class DataSource

   :author: kislay

Methods
-------
getConnection
^^^^^^^^^^^^^

.. java:method:: public Connection getConnection() throws SQLException
   :outertype: DataSource

getInstance
^^^^^^^^^^^

.. java:method:: public static DataSource getInstance(String fileName) throws IOException, SQLException
   :outertype: DataSource

init
^^^^

.. java:method:: public static void init(String fileName) throws IOException, SQLException
   :outertype: DataSource

