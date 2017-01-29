package com.github.kislayverma.rulette.core.ruleinput.type;

import com.github.kislayverma.rulette.core.ruleinput.RuleInput;
import com.github.kislayverma.rulette.core.ruleinput.RuleInputValueFactory;
import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;
import java.io.Serializable;

public class ValueInput extends RuleInput implements Serializable {
    private static final long serialVersionUID = -6340405995013946354L;

    private final IInputValue value;

    public ValueInput(String name, int priority, String inputDataType, String value) throws Exception {
        super(name, priority, RuleInputType.VALUE, inputDataType, value, null);
        this.value = RuleInputValueFactory.getInstance().buildRuleInputVaue(name, value == null ? "" : value);
    }

    @Override
    public boolean evaluate(String value) throws Exception {
        if (this.value.isEmpty()) {
            return true;
        }
        return this.value.compareTo(value) == 0;
    }

    @Override
    public boolean isConflicting(RuleInput input) throws Exception {
        if (!input.getRuleInputDataType().equals(this.getRuleInputDataType())) {
            throw new Exception("Compared rule inputs '" + this.getName() + "' and '"
                    + input.getName() + "' are not the same type.");
        }

        ValueInput castedInput = (ValueInput) input;
        return this.value.equals(castedInput.getValue());
    }

    @Override
    public boolean isBetterFit(RuleInput input) throws Exception {
        // If this is 'Any', it cant be the better fit (unless the other is also
        // 'Any', in which it doesnt matter what we return from here)
        return !this.isAny();
    }

    @Override
    public boolean isAny() {
        return this.value.isEmpty();
    }

    @Override
    public boolean equals(RuleInput otherInput) throws Exception {
        if (this.isAny() && otherInput.isAny()) {
            return true;
        }

        ValueInput castedInput = (ValueInput) otherInput;
        return this.value.equals(castedInput.getValue());
    }

    public IInputValue getValue() {
        return this.value;
    }
}
