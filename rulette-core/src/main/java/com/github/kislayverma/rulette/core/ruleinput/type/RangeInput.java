package com.github.kislayverma.rulette.core.ruleinput.type;

import com.github.kislayverma.rulette.core.ruleinput.RuleInput;
import com.github.kislayverma.rulette.core.metadata.RuleInputMetaData;
import com.github.kislayverma.rulette.core.ruleinput.RuleInputValueFactory;
import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;
import java.io.Serializable;

public class RangeInput extends RuleInput implements Serializable {

    private final IInputValue lowerBound;
    private final IInputValue upperBound;

    public RangeInput(int id, String name, int priority, String inputDataType, String rangeLowerBound, String rangeUpperBound)
            throws Exception {
        this.metaData = new RuleInputMetaData(id, name, priority, RuleInputType.RANGE, inputDataType);

        this.lowerBound = RuleInputValueFactory.getInstance().buildRuleInputVaue(name, rangeLowerBound == null ? "" : rangeLowerBound);
        this.upperBound = RuleInputValueFactory.getInstance().buildRuleInputVaue(name, rangeUpperBound == null ? "" : rangeUpperBound);

    }

    @Override
    public boolean evaluate(String value) throws Exception {
        if (lowerBound.isEmpty() && upperBound.isEmpty()) {
            return true;
        }

        return (lowerBound.compareTo(value) <= 0 && upperBound.compareTo(value) >= 0);
    }

    /**
     * The input rule input conflicts with this if the ranges specified by the
     * two are overlapping.
     *
     * @param input input to be checked for conflict
     * @return true is this rule input conflicts with the one passed in, true otherwise
     * @throws Exception on failure of conflict evaluation
     */
    @Override
    public boolean isConflicting(RuleInput input) throws Exception {
        if (!input.getRuleInputDataType().equals(this.getRuleInputDataType())) {
            throw new Exception("Compared rule inputs '" + this.getName() + "' and '"
                    + input.getName() + "' are not the same type.");
        }

        String inputVal = input.getRawValue();

        // If values are same, or both are 'Any'
        if (this.getRawValue().equals(input.getRawValue())) {
            return true;
        }

        // If only one is 'Any', there is no conflict
        if ("".equals(this.getRawValue()) || "".equals(input.getRawValue())) {
            return false;
        }

        String[] inputArr = inputVal.split("-");

        if ((this.lowerBound.compareTo(inputArr[0]) < 0 && this.upperBound.compareTo(inputArr[0]) <= 0)
                || (this.lowerBound.compareTo(inputArr[1]) > 0 && this.upperBound.compareTo(inputArr[1]) > 0)) {
            return false;
        }

        return true;
    }

    public IInputValue getLowerBound() {
        return lowerBound;
    }

    public IInputValue getUpperBound() {
        return upperBound;
    }
}
