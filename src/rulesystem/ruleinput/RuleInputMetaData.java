package rulesystem.ruleinput;

public class RuleInputMetaData {
	public enum DataType {
		VALUE,
		RANGE
	}

	private int id;
	private int ruleSystemId;
	private String name;
	private int priority;
	private DataType dataType;

	public RuleInputMetaData(int id, int ruleSystemId, String name, int priority, DataType dataType) 
	    throws Exception
	{
		this.id = id;
		this.name = name;
		this.ruleSystemId = ruleSystemId;
		this.priority = priority;
		this.dataType = dataType;
	}

	public int getId() {
		return id;
	}

	public int getRuleSystemId() {
		return ruleSystemId;
	}

	public String getName() {
		return name;
	}

	public int getPriority() {
		return priority;
	}

	public DataType getDataType() {
		return dataType;
	}
}
