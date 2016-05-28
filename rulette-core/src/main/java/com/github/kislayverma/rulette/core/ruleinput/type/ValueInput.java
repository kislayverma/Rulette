package com.github.kislayverma.rulette.core.ruleinput.type;

import com.github.kislayverma.rulette.core.ruleinput.RuleInput;
import com.github.kislayverma.rulette.core.metadata.RuleInputMetaData;
import com.github.kislayverma.rulette.core.ruleinput.RuleInputValueFactory;
import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;
import java.io.Serializable;

public class ValueInput extends RuleInput implements Serializable {

    private final IInputValue value;

    public ValueInput(int id, String name, int priority, String inputDataType, String value)
            throws Exception {
        this.metaData = new RuleInputMetaData(id, name, priority, RuleInputType.VALUE, inputDataType);
        this.value = RuleInputValueFactory.getInstance().buildRuleInputVaue(name, value == null ? "" : value);
    }

    @Override
    public boolean evaluate(String value) throws Exception {
        if (this.value.isEmpty()) {
            return true;
        }
        return this.value.compareTo(value) == 0;
    }

    /**
     * The given input conflicts with this if the values are same.
     *
     * @param input
     * @return 
     * @throws Exception
     */
    @Override
    public boolean isConflicting(RuleInput input) throws Exception {
        if (!input.getRuleInputDataType().equals(this.getRuleInputDataType())) {
            throw new Exception("Compared rule inputs '" + this.getName() + "' and '"
                    + input.getName() + "' are not the same type.");
        }

        return this.getRawValue().equals(input.getRawValue());
    }
}
