package rulesystem.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rulesystem.Rule;
import rulesystem.ruleinput.RuleInputMetaData;
import rulesystem.ruleinput.RuleInputMetaData.DataType;

public class RuleSystemDaoMySqlImpl implements RuleSystemDao {
    private Connection connection = null;
    private final String connString = "jdbc:mysql://localhost/rule_system?user=rs_user&password=rs_user";
    private String tableName;
    private List<RuleInputMetaData> inputColumnList;

    private String uniqueIdColumnName = "id";
    private String uniqueOutputColumnName = "rule_output_id";

    public RuleSystemDaoMySqlImpl(
    	String ruleSystemName, String uniqueIdColName, String uniqueOutputColName)
    {
    	this.uniqueIdColumnName = uniqueIdColName;
    	this.uniqueOutputColumnName = uniqueOutputColName;

    	// This will load the MySQL driver
        try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		}
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}

        initDatabaseConnection();
    	Map<String, String> rsDetailMap = getRuleSystemDetails(ruleSystemName);
    	if (rsDetailMap.isEmpty()) {
    		return;
    	}

    	this.tableName = rsDetailMap.get("table_name");
    }

    @Override
    public boolean isValid() {
    	return (this.tableName == null) ? false : true;
    }

    // Source of the copy-paste : http://www.vogella.com/articles/MySQLJava/article.html
    private void initDatabaseConnection() {
    	if (this.connection == null) {
      	    try {
      	        // Setup the connection with the DB
    	        this.connection = DriverManager.getConnection(connString);
    	    }
      	    catch (Exception e) {
      	    	e.printStackTrace();
      	    }
    	}
    }

	@Override
	public Map<String, String> getRuleSystemDetails(String ruleSystemName) {
    	Map<String, String> rsDetailMap = new HashMap<>();

    	try {
    		Statement statement = connection.createStatement();
			ResultSet resultSet =
				statement.executeQuery("SELECT * FROM rule_system WHERE name LIKE '" + ruleSystemName + "'");

	        while (resultSet.next()) {
	            // It is possible to get the columns via name
	            // also possible to get the columns via the column number
	            // which starts at 1
	            // e.g. resultSet.getSTring(2);
	        	rsDetailMap.put("id", resultSet.getString("id"));
	        	rsDetailMap.put("name", resultSet.getString("name"));
	        	rsDetailMap.put("table_name", resultSet.getString("table_name"));
	        }
	    } catch (SQLException e) {
			e.printStackTrace();
		}

    	return rsDetailMap;
	}

	@Override
	public List<RuleInputMetaData> getInputs(String ruleSystemName) throws Exception {
		List<RuleInputMetaData> inputs = new ArrayList<>();

		try {
    		Statement statement = connection.createStatement();
			ResultSet resultSet =
				statement.executeQuery("SELECT b.* " +
			                           "FROM rule_system AS a " +
						               "JOIN rule_input AS b " +
			                           "    ON b.rule_system_id = a.id " +
						               "WHERE a.name LIKE '" + ruleSystemName + "' " +
			                           "ORDER BY b.priority ASC ");

	        while (resultSet.next()) {
	            DataType dataType =
	            	("Value".equalsIgnoreCase(resultSet.getString("data_type"))) ?
	            		DataType.VALUE : DataType.RANGE;

	            inputs.add(new RuleInputMetaData(resultSet.getInt("id"),
	        			                         resultSet.getInt("rule_system_id"),
			                                     resultSet.getString("name"),
			                                     resultSet.getInt("priority"),
			                                     dataType));
	        }
	    } catch (SQLException e) {
			e.printStackTrace();
		}

		this.inputColumnList = inputs;

		return inputs;
	}

	@Override
	public List<Rule> getAllRules(String ruleSystemName) throws Exception {
		List<Rule> rules = new ArrayList<>();

		try {
    		Statement statement = connection.createStatement();
			ResultSet resultSet =
				statement.executeQuery("SELECT * " + " FROM " + this.tableName);

	        rules = convertToRules(resultSet);
	    } catch (SQLException e) {
			e.printStackTrace();
		}

		return rules;
	}

	private List<Rule> convertToRules(ResultSet resultSet) throws Exception {
		List<Rule> rules = new ArrayList<>();

		while (resultSet.next()) {
        	Map<String, String> inputMap = new HashMap<>();

        	for (RuleInputMetaData col : this.inputColumnList) {
        		inputMap.put(col.getName(), resultSet.getString(col.getName()));
        	}
        	inputMap.put(this.uniqueIdColumnName,
        			     resultSet.getString(this.uniqueIdColumnName));
        	inputMap.put(this.uniqueOutputColumnName,
   			     resultSet.getString(this.uniqueOutputColumnName));

        	rules.add(new Rule(this.inputColumnList, inputMap, this.uniqueIdColumnName, this.uniqueOutputColumnName));
        }

		return rules;
	}

	@Override
	public Rule saveRule(Rule rule) {
		StringBuilder sqlBuilder = new StringBuilder();
		StringBuilder nameListBuilder = new StringBuilder();
		StringBuilder valueListBuilder = new StringBuilder();

		for (RuleInputMetaData col : this.inputColumnList) {
			nameListBuilder.append(col.getName()).append(",");
			String val = rule.getColumnData(col.getName()).getValue();
			valueListBuilder.append((val.isEmpty()) ? null : ("'" + val + "'")).append(",");
		}
		nameListBuilder.append(this.uniqueOutputColumnName).append(",");
		valueListBuilder.append(rule.getColumnData(this.uniqueOutputColumnName).getValue()).append(",");

		sqlBuilder.append("INSERT INTO ")
		          .append(this.tableName)
		          .append(" (").append(nameListBuilder.toString().substring(0, nameListBuilder.length() - 1)).append(") ")
		          .append(" VALUES (").append(valueListBuilder.toString().substring(0, valueListBuilder.length() - 1)).append(") ");
		try {
			PreparedStatement preparedStatement =
			    connection.prepareStatement("SELECT * " + " FROM " + this.tableName);

			if (preparedStatement.executeUpdate(
					sqlBuilder.toString(), Statement.RETURN_GENERATED_KEYS) > 0)
			{
				// Get the rule object for returning using LAST_INSERT_ID() MySql function.
				// This id is maintained per connection so multiple instances inserting rows 
				// isn't a problem.
				preparedStatement =
					    connection.prepareStatement("SELECT * FROM " + this.tableName + 
					    		                    " WHERE rule_id = LAST_INSERT_ID()");
				ResultSet resultSet = preparedStatement.executeQuery();

				return convertToRules(resultSet).get(0);
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public boolean deleteRule(Rule rule) {
		try {
			String sql = "DELETE FROM " + this.tableName +
    				     " WHERE rule_id = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(1, rule.getColumnData(this.uniqueIdColumnName).getValue());

			if (preparedStatement.executeUpdate() > 0) {
				return true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
