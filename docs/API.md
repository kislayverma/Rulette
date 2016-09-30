## APIs
Rulette exposes the following APIs

* List<Rule> getAllRules();    
* Rule getRule(Integer rule_id);    
* Rule getRule(Map<String, String>);    
* Rule addRule(Rule);    
* Rule addRule(Map<String, String>);    
* Rule deleteRule(Rule);    
* Rule deleteRule(Integer rule_id);    
* List<Rule> getConflictingRules(Rule);    
* List<Rule> getAllApplicableRules(Rule);    
* Rule getNextApplicableRule(Map<String, String>);    