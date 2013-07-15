package rulesystem.ruleinput;

import rulesystem.ruleinput.RuleInputMetaData.DataType;

public class ValueInput extends RuleInput{
	private String value;

	public ValueInput (int id, int ruleSystemId, String name, int priority, String value)
		throws Exception
	{
		this.metaData = new RuleInputMetaData(id, ruleSystemId, name, priority, DataType.VALUE);
		this.value = (value == null) ? "" : value;
	}

	@Override
	public boolean evaluate(String value) {
		if (this.value.isEmpty() || this.value.equals(value)) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public String getValue() {
		return value;
	}
}
