## Set up Rulette database
* Execute the setup.sql script on your MySQL server. This creates a database called rule_system
    and creates the necessary tables in it.
* Create a table containing your rules.
* Map this table in the rule_system.rule_system table as shown in the sample-setup.sql script.
* For each rule input, add a row to the rule_system.rule_input table with the input's type (Value/Range), data type and priority order.

## Get rulette-core
Put rulette-core in your Java application's class path. 

If you use maven, you can add the following dependency.


`
<dependency>    
    <groupId>com.github.kislayverma.rulette</groupId>    
    <artifactId>rulette-core</artifactId>      
    <version>1.2.5</version>     
    <scope>compile</scope>    
</dependency>
`
## Let Rulette connect to you DB configuration
Rulette is instantiated by pointing it to a properties file containing the DB credentials to use to connect to the database so that it can read the rule system configuration and the rules. If a file is not provided, it looks for a file name "rulette-datasource.properties" on the classpath.


`
RuleSystem rs = new RuleSystem("dummy-rule-system-name", "dbproperties file path")
`

That's  it! Rulette is all set up and ready to use. Now you play around by calling the different APIs listed on the [API page](https://github.com/kislayverma/Rulette/wiki/APIs).
