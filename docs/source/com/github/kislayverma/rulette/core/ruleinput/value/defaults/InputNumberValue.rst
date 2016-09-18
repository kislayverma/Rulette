.. java:import:: com.github.kislayverma.rulette.core.ruleinput.value IInputValue

.. java:import:: java.io Serializable

InputNumberValue
================

.. java:package:: com.github.kislayverma.rulette.core.ruleinput.value.defaults
   :noindex:

.. java:type::  class InputNumberValue implements IInputValue<Double>, Serializable

Constructors
------------
InputNumberValue
^^^^^^^^^^^^^^^^

.. java:constructor:: public InputNumberValue(String value) throws Exception
   :outertype: InputNumberValue

Methods
-------
compareTo
^^^^^^^^^

.. java:method:: @Override public int compareTo(String obj)
   :outertype: InputNumberValue

getDataType
^^^^^^^^^^^

.. java:method:: @Override public String getDataType()
   :outertype: InputNumberValue

getValue
^^^^^^^^

.. java:method:: @Override public Double getValue()
   :outertype: InputNumberValue

isEmpty
^^^^^^^

.. java:method:: @Override public boolean isEmpty()
   :outertype: InputNumberValue

