## Domain
The domain for Rulette, as for any rule system, comprises of inputs, rules, and outputs.
* A rule maps a tuple of inputs onto an output. Many tuples can map to a single output.
* Inputs can have data types.
* Inputs have priorities.
* We should be able to define a rule for "Any" value of an input.
* Outputs can be anything, so long as the rule system can define and identify them.

e.g.   
[Input 1 = 5, Input 2 = "X", Input 3 = "2016-01-01"] => Rule output "Alpha"
[Input 1 = 5, Input 2 = "", Input 3 = "2017-01-01"] => Rule output 7

![](http://solomonsays.in/web/imgs/rulette-domain.png)    

A rule system is responsible for defining inputs and outputs, storing rules, and evaluating a given set on input values against rules to find the best matching rule.

## Rule definition
Rules map inputs onto outputs, and there are multiple ways of doing this.

* You can map each individual possible value of an input to an output by defining that many rules. These inputs are defined as "VALUE" type. e.g. [Input name = "Obama" => Output "President"] 
* Map ranges of input values onto an output. These inputs are marked as "RANGE" and Rulette the prescribed (customizable) way of determining if an input falls in the defined range and hence fits a certain rule. [Input price = "0-500" => Output "No tax"]

## Data Model
![](http://solomonsays.in/web/imgs/rulette-modelling.png)

### Rule Systems
Rule system is the topmost entity in Rulette. It is identified by a unique name and id in a table called "rule_system". You can define the name of the rule unique column and the output column of the rule table

### Rule Inputs
Each rule system takes multiple inputs, each with a specified priority. Inputs are stored in the "rule_input" table and mapped to their respective rule system via its id. 

### Rules
Rules pertaining to each rule system are stored in different tables which must be defined the "rule_system" table. The names of the columns of the rule table must be "exactly" the same as the name of the defined rule inputs. 

## Rule Evaluation
On instantiation, Rulette loads all the rules in memory and then executes all evaluation without touching the database. This makes for lightning fast responses and great DB level scalability. We have tested the performance with up to 75K rules of 8 inputs each being held in memory and the setup worked just fine without GC hiccups etc.

That said, if you have millions of rules, you would want to keep a close eye on your memory metrics as you use Rulette.