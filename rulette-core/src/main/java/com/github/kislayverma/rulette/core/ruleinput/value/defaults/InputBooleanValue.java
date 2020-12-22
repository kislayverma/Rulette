package com.github.kislayverma.rulette.core.ruleinput.value.defaults;

import java.io.Serializable;

import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;

public class InputBooleanValue implements IInputValue<Boolean>, Serializable {

    private static final long serialVersionUID = -474469687493839730L;

    private final Boolean value;

    InputBooleanValue(String value){
        this.value = Boolean.valueOf(value);
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
    public int compareTo(String obj) {
        return this.value.compareTo(Boolean.valueOf(obj));
    }

    @Override
    public int compareTo(IInputValue<Boolean> obj) {
        return this.compareTo(obj);
    }

    @Override
    public boolean isEmpty() {
        return this.value == Boolean.FALSE;
    }
    
}
