package com.github.kislayverma.rulette.core.ruleinput.type;

import com.github.kislayverma.rulette.core.ruleinput.RuleInput;
import com.github.kislayverma.rulette.core.metadata.RuleInputMetaData;
import com.github.kislayverma.rulette.core.ruleinput.RuleInputValueFactory;
import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;
import java.io.Serializable;

public class RangeInput extends RuleInput implements Serializable {
    private static final long serialVersionUID = 7246688913819092267L;

    private final IInputValue lowerBound;
    private final IInputValue upperBound;

    public RangeInput(int id, String name, int priority, String inputDataType, String value)
            throws Exception {
        this.metaData = new RuleInputMetaData(id, name, priority, RuleInputType.RANGE, inputDataType);
        String[] rangeArr = value.split("-");

        if (value.isEmpty()) {
            // The'any' case
            this.lowerBound = RuleInputValueFactory.getInstance().buildRuleInputVaue(name, "");
            this.upperBound = RuleInputValueFactory.getInstance().buildRuleInputVaue(name, "");
        } else if (rangeArr.length < 2) {
            throw new Exception("Improper value for field " + this.metaData.getName()
                    + ". Range fields must be given in the format 'a-b' (with "
                    + "a and b as inclusive lower and upper bound respectively.)");
        } else {
            this.lowerBound = RuleInputValueFactory.getInstance().buildRuleInputVaue(name, rangeArr[0] == null ? "" : rangeArr[0]);
            this.upperBound = RuleInputValueFactory.getInstance().buildRuleInputVaue(name, rangeArr[1] == null ? "" : rangeArr[1]);
        }
    }

    @Override
    public boolean evaluate(String value) throws Exception {
        // No bounds are defined, eseentially same as 'Any'
        if (lowerBound.isEmpty() && upperBound.isEmpty()) {
            return true;
        }

        // If 'Any' is asked but something is defined(lower or upper), this does not match
        if (value == null && (!lowerBound.isEmpty() || !upperBound.isEmpty())) {
            return false;
        }

        return (lowerBound.compareTo(value) <= 0 && upperBound.compareTo(value) >= 0);
    }

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

    @Override
    public boolean isBetterFit(RuleInput input) throws Exception {
        String inputVal = input.getRawValue();
        String[] inputArr = inputVal.split("-");

        // If the other input is 'Any', this input can only be equal or better, never worse.
        // And vice-versa
        if ("".equals(inputVal)) {
            return true;
        } else if (this.lowerBound.isEmpty() && this.upperBound.isEmpty()) {
            return false;
        }

        // If this range starts from -INFiNITY
        if (this.lowerBound.isEmpty()) {
            // If this input's upper bound is lesser than the other's it is a better fit
            if (this.upperBound.compareTo(inputArr[1]) < 0) {
                return true;
            }
        } else if (this.upperBound.isEmpty()) {
            // If this range ends at +INFINITY, this input will be better fit if its lower bound is
            // greater than the other ones
            if (this.lowerBound.compareTo(inputArr[0]) > 0) {
                return true;
            }
        } else if ("".equals(inputArr[0])) {
            // If other input start at -INFINITY, this input will be better if it ends lower than the other
            if (this.upperBound.compareTo(inputArr[1]) < 0) {
                return true;
            }
        } else if ("".equals(inputArr[1])) {
            // If other input ends at +INFINITY, this input will be better if it starts higher than the other
            if (this.lowerBound.compareTo(inputArr[0]) > 0) {
                return true;
            }
        } else {
            // If INFINITY is not involved, then simply compare bounds
            if (this.lowerBound.compareTo(inputArr[0]) > 0 && this.upperBound.compareTo(inputArr[1]) < 0) {
                return true;
            }
        }

        return false;
    }
}
