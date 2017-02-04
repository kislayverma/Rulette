package com.github.kislayverma.rulette.core.ruleinput;

import com.github.kislayverma.rulette.core.metadata.RuleInputMetaData;
import com.github.kislayverma.rulette.core.ruleinput.type.RuleInputType;
import java.io.Serializable;

public abstract class RuleInput implements Serializable {
    private static final long serialVersionUID = 2370282382386651591L;

    protected RuleInputMetaData metaData;
    protected String rawInput;

    protected RuleInput(String name, int priority, RuleInputType ruleInputType, 
        String inputDataType, String rangeLowerBound, String rangeUpperBound) throws Exception {

        this.metaData = new RuleInputMetaData(
            name, priority, ruleInputType, inputDataType, rangeLowerBound, rangeUpperBound);
        if (ruleInputType == RuleInputType.VALUE) {
            this.rawInput = (rangeLowerBound == null ? "" : rangeLowerBound);
        } else {
            this.rawInput = (rangeLowerBound == null ? "" : rangeLowerBound) + "-" +
                (rangeUpperBound == null ? "" : rangeUpperBound);
        }
    }

    /**
     * This method matches the given value against this rule input and returns true if it fits.
     * For value inputs, match means either same value or 'Any'. For Range input, match means 
     * 'Any' or the value should fall within the defined range of the input.
     * @param value The value to compare against this input
     * @return true if the value matches the input definition, false otherwise
     * @throws Exception on any error in evaluation
     */
    public abstract boolean evaluate(String value) throws Exception;

    /**
     * This method determines if this rule input conflicts with the given input. For value inputs,
     * conflict means having the same value. For range inputs, conflict means having partially 
     * overlapping range (e.g [1,5] and [2,10]). Ranges DO NOT conflict if one is completely 
     * contained within the other.
     * @param input The rule input to compare with
     * @return true if inputs are conflicting
     * @throws Exception on any error in evaluation
     */
    public abstract boolean isConflicting(RuleInput input) throws Exception;

    /**
     * This method is used to determine if this rule input is a better than the given rule input
     * for the same value. It assumes that both inputs match the value and that they are non-conflicting.
     * 
     * @param input The rule input to be matched against
     * @return 0 if both input are identical in fit
     *         1 if this input is a better fit
     *         -1 if this input is not the better fit
     * @throws Exception on any error in evaluation
     */
    public abstract int isBetterFit(RuleInput input) throws Exception;

    public final String getRawValue() {
        return this.rawInput;
    }

    public String getName() {
        return this.metaData.getName();
    }

    public int getPriority() {
        return this.metaData.getPriority();
    }

    public RuleInputType getRuleInputType() {
        return this.metaData.getRuleInputType();
    }

    public String getRuleInputDataType() {
        return this.metaData.getDataType();
    }

    /**
     * This method returns true if this rule input is of the 'Any' (match all) type.
     * 
     * @return true if input is 'Any', false otherwise
     */
    public abstract boolean isAny();

    /**
     * This method returns true if this input is exactly same as the given other input.
     * 
     * @param otherInput The rule input to compare against
     * @return true if this and the given inputs are exactly same, false otherwise
     * @throws Exception on error in evaluation
     */
    public abstract boolean equals(RuleInput otherInput) throws Exception;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.metaData.getName())
                .append(":")
                .append(this.rawInput)
                .append("\t");
        return builder.toString();
    }
}
