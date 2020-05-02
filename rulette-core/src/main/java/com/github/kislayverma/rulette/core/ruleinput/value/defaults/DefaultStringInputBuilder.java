package com.github.kislayverma.rulette.core.ruleinput.value.defaults;

import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;
import com.github.kislayverma.rulette.core.ruleinput.value.IInputValueBuilder;

public class DefaultStringInputBuilder implements IInputValueBuilder<String>{

    @Override
    public IInputValue<String> build(String value) {
        return new InputStringValue(value);
    }
}
