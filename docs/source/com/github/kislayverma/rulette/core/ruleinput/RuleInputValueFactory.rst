.. java:import:: com.github.kislayverma.rulette.core.ruleinput.value IInputValue

.. java:import:: com.github.kislayverma.rulette.core.ruleinput.value IInputValueBuilder

.. java:import:: java.util Map

.. java:import:: java.util.concurrent ConcurrentHashMap

.. java:import:: org.slf4j Logger

.. java:import:: org.slf4j LoggerFactory

RuleInputValueFactory
=====================

.. java:package:: com.github.kislayverma.rulette.core.ruleinput
   :noindex:

.. java:type:: public class RuleInputValueFactory

Methods
-------
buildRuleInputVaue
^^^^^^^^^^^^^^^^^^

.. java:method:: public IInputValue buildRuleInputVaue(String ruleInputName, String rawValue) throws Exception
   :outertype: RuleInputValueFactory

getInstance
^^^^^^^^^^^

.. java:method:: public static RuleInputValueFactory getInstance()
   :outertype: RuleInputValueFactory

registerRuleInputBuilder
^^^^^^^^^^^^^^^^^^^^^^^^

.. java:method:: public void registerRuleInputBuilder(String ruleInputName, IInputValueBuilder builder)
   :outertype: RuleInputValueFactory

