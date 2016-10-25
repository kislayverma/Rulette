package com.github.kislayverma.rulette.core.ruleinput.type;

import com.github.kislayverma.rulette.core.ruleinput.RuleInput;
import com.github.kislayverma.rulette.core.ruleinput.RuleInputValueFactory;
import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;
import java.io.Serializable;

public class RangeInput extends RuleInput implements Serializable {
    private static final long serialVersionUID = 7246688913819092267L;

    private final IInputValue lowerBound;
    private final IInputValue upperBound;

    public RangeInput(String name, int priority, String inputDataType, String value) throws Exception {
        super(name, priority, RuleInputType.RANGE, inputDataType, value);

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
        if (this.isAny()) {
            // Everything matches 'Any'
            return true;
        } else if (value == null) {
            // If 'Any' is asked but this input isn't 'Any'), this does not match
            return false;
        } else {
            return (lowerBound.compareTo(value) <= 0 && upperBound.compareTo(value) >= 0);
        }
    }

    @Override
    public boolean isConflicting(RuleInput input) throws Exception {
        if (!input.getRuleInputDataType().equals(this.getRuleInputDataType())) {
            throw new Exception("Compared rule inputs '" + this.getName() + "' and '"
                    + input.getName() + "' are not the same type.");
        }

        // If both are exactly equal ('Any' or otherwise)
        if (this.equals(input)) {
            return true;
        } else if (this.isAny() || input.isAny()) {
            // Since they are not equal, they can't cpnflict if either is 'Any'
            return false;
        } else {
            // If they are neither equal nor is either of them 'Any', we have to check for overlap
            RangeInput castedInput = (RangeInput) input;

            if ((this.lowerBound.compareTo(castedInput.getLowerBound()) < 0 && this.upperBound.compareTo(castedInput.getLowerBound()) <= 0)
                    || (this.lowerBound.compareTo(castedInput.getUpperBound()) > 0 && this.upperBound.compareTo(castedInput.getUpperBound()) > 0)) {
                return false;
            }

            return true;
        }
    }

    @Override
    public boolean isBetterFit(RuleInput input) throws Exception {
        // If the other input is 'Any', this input can only be equal or better, never worse.
        // And vice-versa
        if (input.isAny()) {
            return true;
        } else if (this.isAny()) {
            return false;
        }

        RangeInput castedInput = (RangeInput) input;

        // If this range starts from -INFiNITY
        if (this.lowerBound.isEmpty()) {
            // If this input's upper bound is lesser than the other's it is a better fit
            if (this.upperBound.compareTo(castedInput.getUpperBound()) < 0) {
                return true;
            }
        } else if (this.upperBound.isEmpty()) {
            // If this range ends at +INFINITY, this input will be better fit if its lower bound is
            // greater than the other ones
            if (this.lowerBound.compareTo(castedInput.getLowerBound()) > 0) {
                return true;
            }
        } else if (castedInput.getLowerBound().isEmpty()) {
            // If other input start at -INFINITY, this input will be better if it ends lower than the other
            if (this.upperBound.compareTo(castedInput.getUpperBound()) < 0) {
                return true;
            }
        } else if (castedInput.getUpperBound().isEmpty()) {
            // If other input ends at +INFINITY, this input will be better if it starts higher than the other
            if (this.lowerBound.compareTo(castedInput.getLowerBound()) > 0) {
                return true;
            }
        } else {
            // If INFINITY is not involved, then simply compare bounds
            if (this.lowerBound.compareTo(castedInput.getLowerBound()) > 0 &&
                    this.upperBound.compareTo(castedInput.getUpperBound()) < 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isAny() {
        return (this.lowerBound.isEmpty() && this.upperBound.isEmpty());
    }

    @Override
    public boolean equals(RuleInput otherInput) {
        if (this.isAny() && otherInput.isAny()) {
            return true;
        } else if (this.isAny() && !otherInput.isAny()) {
            return false;
        } else if (!this.isAny() && otherInput.isAny()) {
            return false;
        } else {
            RangeInput castedInput = (RangeInput) otherInput;

            return this.lowerBound.compareTo(castedInput.getLowerBound()) == 0 &&
                   this.upperBound.compareTo(castedInput.getUpperBound()) == 0;
        }
    }

    public IInputValue getLowerBound() {
        return this.lowerBound;
    }

    public IInputValue getUpperBound() {
        return this.upperBound;
    }
}
