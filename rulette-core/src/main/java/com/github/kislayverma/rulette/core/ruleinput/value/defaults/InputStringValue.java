package com.github.kislayverma.rulette.core.ruleinput.value.defaults;

import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;
import java.io.Serializable;

class InputStringValue implements IInputValue<String>, Serializable {

    private final String value;

    public InputStringValue (String value) throws Exception {
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
        String x = (o == null) ? "" : o;
        return this.value.compareTo(x);
    }

    @Override
    public String getDataType() {
        return String.class.getName();
    }
}
