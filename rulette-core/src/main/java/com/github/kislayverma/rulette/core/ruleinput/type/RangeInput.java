package com.github.kislayverma.rulette.core.ruleinput.type;

import com.github.kislayverma.rulette.core.ruleinput.RuleInput;
import com.github.kislayverma.rulette.core.ruleinput.RuleInputValueFactory;
import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;
import java.io.Serializable;

public class RangeInput extends RuleInput implements Serializable {
    private static final long serialVersionUID = 7246688913819092267L;

    private final IInputValue lowerBound;
    private final IInputValue upperBound;

    public RangeInput(String name, int priority, String inputDataType, String lowerBound, String upperBound) {
        super(name, priority, RuleInputType.RANGE, inputDataType, lowerBound, upperBound);

        this.lowerBound = RuleInputValueFactory.getInstance().buildRuleInputVaue(name, lowerBound == null ? "" : lowerBound);
        this.upperBound = RuleInputValueFactory.getInstance().buildRuleInputVaue(name, upperBound == null ? "" : upperBound);
        // If upper bound is open, the compareTo evaluates will be > 0, but it is actually 
        // legit (being an open range to infinity
        if (!this.lowerBound.isEmpty() && !this.upperBound.isEmpty() && this.lowerBound.compareTo(upperBound) > 0) {
            throw new IllegalArgumentException(
                "Lower bound greater than upper bound for field : " + this.metaData.getName());
        }
    }

    @Override
    public boolean evaluate(String value) {
        if (this.isAny()) {
            // Everything matches 'Any'
            return true;
        } else if (value == null) {
            // If 'Any' is asked but this input isn't 'Any'), this does not match
            return false;
        } else {
            if ((lowerBound.compareTo(value) <= 0 && upperBound.isEmpty()) ||
                (lowerBound.isEmpty() && upperBound.compareTo(value) >= 0) ||
                (lowerBound.compareTo(value) <= 0 && upperBound.compareTo(value) >= 0)) {
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean isConflicting(RuleInput input) {
        if (!input.getRuleInputDataType().equals(this.getRuleInputDataType())) {
            throw new IllegalArgumentException("Compared rule inputs '" + this.getName() + "' and '"
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
    public int isBetterFit(RuleInput input) {
        // If both are 'Any', then they are the same.
        // If the other input is 'Any', this input can only be equal or better, never worse.
        // And vice-versa.
        if (this.equals(input)) {
            return 0;
        } else if (this.isAny()) {
            return -1;
        } else if (input.isAny()) {
            return 1;
        }

        RangeInput castedInput = (RangeInput) input;

        // If this range starts from -INFiNITY
        if (this.lowerBound.isEmpty()) {
            // If this input's upper bound is lesser than the other's it is a better fit
            if (this.upperBound.compareTo(castedInput.getUpperBound()) < 0) {
                return 1;
            }
        } else if (this.upperBound.isEmpty()) {
            // If this range ends at +INFINITY, this input will be better fit if its lower bound is
            // greater than the other ones
            if (this.lowerBound.compareTo(castedInput.getLowerBound()) > 0) {
                return 1;
            }
        } else if (castedInput.getLowerBound().isEmpty()) {
            // If other input start at -INFINITY, this input will be better if it ends lower than the other
            if (this.upperBound.compareTo(castedInput.getUpperBound()) < 0) {
                return 1;
            }
        } else if (castedInput.getUpperBound().isEmpty()) {
            // If other input ends at +INFINITY, this input will be better if it starts higher than the other
            if (this.lowerBound.compareTo(castedInput.getLowerBound()) > 0) {
                return 1;
            }
        } else {
            // If INFINITY is not involved, then simply compare bounds
            if (this.lowerBound.compareTo(castedInput.getLowerBound()) > 0 &&
                    this.upperBound.compareTo(castedInput.getUpperBound()) < 0) {
                return 1;
            }
        }

        return -1;
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

            // Note : While comparing dates for rules, we're taking care of checking null values for lower and
            // upper bounds before actually comparing them using the 'compareTo' method, so as to safeguard from null values.

            if(this.lowerBound.getValue() == null && this.upperBound.getValue() != null
                    && castedInput.getLowerBound().getValue() != null && castedInput.upperBound.getValue() == null){
                return false;
            }

            if(this.lowerBound.getValue() != null && this.upperBound.getValue() == null
                    && castedInput.getLowerBound().getValue() == null && castedInput.upperBound.getValue() != null){
                return false;
            }

            if(this.lowerBound.getValue() == null && this.upperBound.getValue() != null
                    && castedInput.getLowerBound().getValue() == null && castedInput.upperBound.getValue() != null){
                return this.upperBound.compareTo(castedInput.getUpperBound()) == 0;
            }

            if(this.lowerBound.getValue() != null && this.upperBound.getValue() == null
                    && castedInput.getLowerBound().getValue() != null && castedInput.upperBound.getValue() == null){
                return this.lowerBound.compareTo(castedInput.getLowerBound()) == 0;
            }

            if(this.lowerBound.getValue() == null && this.upperBound.getValue() != null
                    && castedInput.getLowerBound().getValue() == null && castedInput.getUpperBound().getValue() != null){
                return this.getUpperBound().compareTo(castedInput.getUpperBound()) == 0;
            }

            if(this.lowerBound.getValue() != null && this.upperBound.getValue() == null
                    && castedInput.getLowerBound().getValue() != null && castedInput.getUpperBound().getValue() == null){
                return this.lowerBound.compareTo(castedInput.getLowerBound()) == 0;
            }

            if(this.lowerBound.getValue() != null && this.upperBound.getValue() != null
                    && castedInput.getLowerBound().getValue() != null && castedInput.getUpperBound().getValue() != null){
                return this.lowerBound.compareTo(castedInput.getLowerBound()) == 0 &&
                        this.upperBound.compareTo(castedInput.getUpperBound()) == 0;
            }

            return false;
        }
    }

    public IInputValue getLowerBound() {
        return this.lowerBound;
    }

    public IInputValue getUpperBound() {
        return this.upperBound;
    }
}
