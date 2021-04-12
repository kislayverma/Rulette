package com.github.kislayverma.rulette.core.ruleinput.value.defaults;

import java.io.Serializable;

import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;

public class InputBooleanValue implements IInputValue<Boolean>, Serializable {

    private static final long serialVersionUID = -474469687493839730L;

    private final Boolean value;

    InputBooleanValue(String value) {
        this.value = value == null || value.isEmpty() ? null : Boolean.valueOf(value);
    }

    @Override
    public String getDataType() {
        return Boolean.class.getName();
    }

    @Override
    public Boolean getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        IInputValue<Boolean> that = (IInputValue<Boolean>) obj;
        if (this.isEmpty() && that.isEmpty()) {
            return true;
        } else if (!this.isEmpty()) {
            return this.value.equals(that.getValue());
        } else {
            return that.getValue().equals(this.value);
        }
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
            return this.value.compareTo(Boolean.valueOf(obj));
        }
    }

    @Override
    public int compareTo(IInputValue obj) {
        if ((obj == null) && (this.value == null)) {
            return 0;
        } else if (obj == null || obj.isEmpty()) {
            return 1;
        } else if (this.value == null) {
            return -1;
        } else {
            return this.value.compareTo((Boolean)obj.getValue());
        } 
    }

    @Override
    public boolean isEmpty() {
        return this.value == null;
    }

}
