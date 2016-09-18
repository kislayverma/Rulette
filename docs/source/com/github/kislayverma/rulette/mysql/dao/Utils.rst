.. java:import:: java.io File

.. java:import:: java.io IOException

.. java:import:: java.io InputStream

.. java:import:: java.net URL

.. java:import:: java.util Properties

Utils
=====

.. java:package:: com.github.kislayverma.rulette.mysql.dao
   :noindex:

.. java:type:: public class Utils

   :author: kislay

Methods
-------
readProperties
^^^^^^^^^^^^^^

.. java:method:: public static Properties readProperties(String fileName) throws IOException
   :outertype: Utils

   Read a properties file from the class path and return a Properties object

   :param fileName: file to read
   :throws IOException: on file reading error
   :return: Properties object loaded with properties from the given file

