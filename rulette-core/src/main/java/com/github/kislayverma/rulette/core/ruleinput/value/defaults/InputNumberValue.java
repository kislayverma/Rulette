package com.github.kislayverma.rulette.core.ruleinput.value.defaults;

import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;
import java.io.Serializable;

class InputNumberValue implements IInputValue<Double>, Serializable {
    private static final long serialVersionUID = 7241569969469955107L;

    private final Double value;

    public InputNumberValue (String value) throws Exception {
        this.value = value == null || value.isEmpty() ? null : Double.parseDouble(value);
    }

    @Override
    public boolean isEmpty() {
        return this.value == null;
    }

    @Override
    public int compareTo(String obj) {
        if ((obj == null || "".equals(obj)) && (this.value == null)) {
            return 0;
        } else if (obj == null || "".equals(obj)) {
            return 1;
        } else if (this.value == null) {
            return -1;
        } else {
            return this.value.compareTo(Double.parseDouble(obj));
        }
    }

    @Override
    public Double getValue() {
        return this.value;
    }

    @Override
    public String getDataType() {
        return Double.class.getName();
    }

    @Override
    public boolean equals(Object obj) {
        IInputValue<Double> that = (IInputValue<Double>) obj;
        if (this.isEmpty() && that.isEmpty()) {
            return true;
        } else if (!this.isEmpty()) {
            return this.value.equals(that.getValue());
        } else {
            return that.getValue().equals(this.value);
        }
    }

    @Override
    public int compareTo(IInputValue<Double> obj) {
        if (this.isEmpty() && obj.isEmpty()) {
            return 0;
        } else {
            return this.value.compareTo(obj.getValue());
        }
    }
}
