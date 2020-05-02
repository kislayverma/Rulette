package com.github.kislayverma.rulette.core.ruleinput.value.defaults;

import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;
import com.github.kislayverma.rulette.core.ruleinput.value.IInputValueBuilder;

public class DefaultNumberInputBuilder implements IInputValueBuilder<Double>{

    @Override
    public IInputValue<Double> build(String value) {
        return new InputNumberValue(value);
    }
}
