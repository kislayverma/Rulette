## What is Rulette
Rulette is a lightweight, domain-agnostic rule modelling, storage, and evaluation engine.    

## Rulette is a rule engine
A rule engine allows the user to store rules, load rules and to evaluate them against a given input arrive at a deterministic output. Rulette does exactly this. It allow you to create a bunch of rules with minimal boilerplate configuration.

## Rulette is a multi-tenant rule engine
Rulette allow users to define multiple rule engines in the same "deployment", so to speak. You just configure multiple rule system each with its own sets of rules with as little as a few lines of SQL.

## Rulette does things out-of-the-box
Rulette is targeted at practical usages of rule system and doesn't try to cover every possible eventuality. Compared to some other established products like [Apache Drools](http://www.drools.org), a lot of things have been left out.  To compensate Rulette provides a whole bunch of functionality out of the box. 
* Common input data types - Rulette has in-built support for strings, numbers, and dates as rule inputs.
* Custom rule inputs - If that doesnt work for you, you can define custom data types(Employee, Vehicle etc.) as inputs. It is easy to define their complete behaviour of these types which just a couple of classes.
* Mapping input ranges to outputs - Ranges are first class citizens in Rulette.

## Rulette doesn't mess with your business
Rulette does not try to "figure out" your business and its use cases and stays strictly neutral in terms of inputs and outputs. It is left to the user to decide how to interpret the result of the rule engine. This allows for great flexibility in its application. On the client side, it means clean code which is necessarily decoupled from rules, because Rulette won't let it get coupled.

## Rulette can run standalone
If you do not have a Java application to use Rulette but would still like to partake of the awesomeness, Rulette also ships as a standalone executable jar which exposes all the rule system capabilities as REST APIs!!! You set up Rulette in the usual way, point rulette-rest jar to the configuration and Voila!!! You have your rule system as a service. No container, no management, no hassle.

## What next
* Get cracking via our [Quick Start guide](https://github.com/kislayverma/Rulette/wiki/Quick-start).
* See some example under the [rulette-examples](https://github.com/kislayverma/Rulette/tree/master/rulette-examples/src/main/java/com/github/kislayverma/rulette/example) module.   
* Deep dive into the [innards of Rulette](https://github.com/kislayverma/Rulette/wiki/Architecture) here.   
* Browse the [javadoc](https://github.com/kislayverma/Rulette/wiki/Javadoc)
