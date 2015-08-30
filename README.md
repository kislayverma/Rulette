# Rulette
This is a lightweight, domain-agnostic implementation intended to model a majority of usual business rules. This is a generic rule definition and evaluation engine, and it has to be wrapped in a domain aware wrapper to be used in a business context.

 1. JARGON : A rule-system is a mapping of elements of an input space comprising of one
 or more distinct inputs to a well-defined output space.
 2. JARGON : Each element of the input space is called a rule-input. An input can be of type 'value' (discrete valued variable) or a range (intervals).
 3. JARGON : The output of the rule engine is an identifier which is  intrinsically meaningless in this system but meaningful in the business use-case (i.e. primary key of an output data table).
 4. An example of rule : If X= 2 AND Y = 3, THEN Z =42 (X and Y are rule-inputs of type value).
 5. An example of rule : If X= 2 AND 20120101 < Y < 20131231, THEN Z =42 (Y is a rule-iinputs of type range).
 6. Rules are modelled as rows of a database table which has one column per rule-input
 7. Values of all columns are AND-ed together for evaluating the rule.
 8. A null value of any rule-input means that rule-input has no part to play in the evaluation of that rule. e.g. in the rule : IF X = 2 and Y = null, Z = 42, the output will be 42 for all values of Y so long as X is 2.
 6. Rule inputs can be of type number(float), date, or stringare treated as strings.
 7. Input have a priority order. This is the order in which they are evaluated to arrive at the
    output. Defining priorities is much like defining database indexes - different choices can 
    cause widely divergent performance. Worse-depending on your domain, incorrect priorities may 
    even lead to incorrect results.
 8. The table which holds the rules must have two columns : 'rule_id' (unique id for the rule, preferable an auto-incrementing primary key) and 'rule_output_id' (unique identifier for the output).
 9. To do rule evaluation, the system takes the combination of the different rule inputs given 
    to it, and returns the best fitting rule (if any). 'Best fit' means:
    a. Value inputs - An exact value match is better than an 'any' match. e.g. if there are two 
       rules, one with value of input X as 1 and the other as any, then on passing X = 1, the 
       former rule will be returned. On passing X = 2, the latter will be returned (as the 
       former obviously doesn't match).
    b. Range inputs : A tighter range is a better fit than a wider range. e.g. if there are two 
       rules, one with value of input X as Jan 1 2013 to Dec31, 2013 and the other as Feb 1 2013 
       to March 1 2013, then on passing X = Feb 15, 2013, the latter will be returned.
 10. Conflicting rules are those that will, if present in the system, cause ambiguity at the time 
     of rule evaluation. The addRule APIs provided do not allow addition of conflicting rules.
 
The following APIs are exposed for interacting with the rule system:
```
List<Rule> getAllRules()
Rule getRule(Integer rule_id)
Rule getRule(Map<String, String>)
Rule addRule(Rule)
Rule addRule(Map<String, String>)
Rule deleteRule(Rule)
Rule deleteRule(Integer rule_id)
List<Rule> getConflictingRules(Rule)
Rule getNextApplicableRule(Map<String, String>)
```
-------------------------
#  Pre-requisites
 1. Java 1.7
 2. MySQL 5.x
-------------------------- 
#  Setup
 1. Execute the setup.sql script on your MySQL server. This creates a database called rule_system
    and creates the necessary table in it.
 2. Create a table containing your rules as defined in #8 above (if you don't have it already).
 3. Map this table in the rule_system.rule_system table as shown in the sample-setup.sql script.
 4. For each rule input, add a row to the rule_system.rule_input table with the input's type 
    (Value/Range) and priority order.
 5. Put the jar in your class path.
 
 That's  it! The rule system is all set up and ready to use.
------------------------------------------------------
#  Sample usage #

```
 RuleSystem rs = new RuleSystem(<rule system name as configured>[, <validator>]);
 Rule r = rs.getRule(<ruleid>);
```
