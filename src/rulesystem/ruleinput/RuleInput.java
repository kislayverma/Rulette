package rulesystem.ruleinput;

import rulesystem.ruleinput.RuleInputMetaData.DataType;

public abstract class RuleInput {
	protected RuleInputMetaData metaData;

	public static RuleInput createRuleInput(int id,
			                                int ruleSystemId,
			                                String name,
			                                int priority,
			                                DataType dataType,
			                                String value)
			throws Exception
	{
		switch(dataType) {
		    case VALUE:
		    	return new ValueInput(id, ruleSystemId, name, priority, value);
		    case RANGE:
		    	return new RangeInput(id, ruleSystemId, name, priority, value);
		    default:
		    	return null;
		}
	}

	public abstract boolean evaluate(String value);

	public abstract String getValue();

	public int getId() {
		return this.metaData.getId();
	}

	public int getRuleSystemId() {
		return this.metaData.getRuleSystemId();
	}

	public String getName() {
		return this.metaData.getName();
	}

	public int getPriority() {
		return this.metaData.getPriority();
	}

	public DataType getDataType() {
		return this.metaData.getDataType();
	}
}
