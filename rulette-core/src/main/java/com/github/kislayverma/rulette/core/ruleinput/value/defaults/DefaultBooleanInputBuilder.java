package com.github.kislayverma.rulette.core.ruleinput.value.defaults;

import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;
import com.github.kislayverma.rulette.core.ruleinput.value.IInputValueBuilder;

public class DefaultBooleanInputBuilder implements IInputValueBuilder<Boolean> {
    
    @Override
    public IInputValue<Boolean> build(String value) {
        return new InputBooleanValue(value);
    }
    
}
