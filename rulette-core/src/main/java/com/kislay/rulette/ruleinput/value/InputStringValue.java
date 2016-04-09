package com.kislay.rulette.ruleinput.value;

import java.io.Serializable;

public class InputStringValue extends RuleInputValue implements IInputValue<String>, Serializable {

    private final String value;

    public InputStringValue (String value) throws Exception {
        this.dataType = InputDataType.STRING;
        this.value = value;
    }

    @Override
    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public int compareTo(String o) {
        return this.value.compareTo(o);
    }
}
