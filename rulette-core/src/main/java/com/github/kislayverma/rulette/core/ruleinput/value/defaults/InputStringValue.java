package com.github.kislayverma.rulette.core.ruleinput.value.defaults;

import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;
import java.io.Serializable;

class InputStringValue implements IInputValue<String>, Serializable {
    private static final long serialVersionUID = 8248443566883409848L;

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

    @Override
    public int compareTo(IInputValue<String> obj) {
        if (this.isEmpty() && obj.isEmpty()) {
            return 0;
        } else {
            return this.value.compareTo(obj.getValue());
        }
    }

    @Override
    public boolean equals(Object obj) {
        IInputValue<String> that = (IInputValue<String>) obj;
        if (this.isEmpty() && that.isEmpty()) {
            return true;
        } else if (!this.isEmpty()) {
            return this.value.equals(that.getValue());
        } else {
            return that.getValue().equals(this.value);
        }
    }
}
