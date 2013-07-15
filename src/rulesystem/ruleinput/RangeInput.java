package rulesystem.ruleinput;

import rulesystem.ruleinput.RuleInputMetaData.DataType;

public class RangeInput extends RuleInput{
	private String lowerBound;
	private String upperBound;

	public RangeInput (int id, int ruleSystemId, String name, int priority, String value)
		throws Exception
	{
		this.metaData = new RuleInputMetaData(id, ruleSystemId, name, priority, DataType.VALUE);
		String[] rangeArr = value.split("-");

		if (value == null || value.isEmpty()) {
			// The'any' case
			this.lowerBound = "";
			this.upperBound = "";
		}
		else if (rangeArr.length < 2) {
			throw new Exception("Improper value for field " + this.metaData.getName() +
					            ". Range fields must be given in the format 'a-b' (with " +
					            "a and b as inclusive lower and upper bound respectively.)");
		}
		else {
			this.lowerBound = rangeArr[0];
			this.upperBound = rangeArr[1];
		}
	}

	@Override
	public boolean evaluate(String value) {
		if (lowerBound.isEmpty() && upperBound.isEmpty()) {
			return true;
		}

		if ((lowerBound.compareTo(value) <= 0) && (upperBound.compareTo(value) >= 0)) {
			return true;
		}

		return false;
	}

	@Override
	public String getValue() {
		return (lowerBound.isEmpty() && upperBound.isEmpty())
			       ? "" : lowerBound + "-" + upperBound;
	}
}
