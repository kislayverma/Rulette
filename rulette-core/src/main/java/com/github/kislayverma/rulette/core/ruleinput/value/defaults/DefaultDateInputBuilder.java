package com.github.kislayverma.rulette.core.ruleinput.value.defaults;

import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;
import com.github.kislayverma.rulette.core.ruleinput.value.IInputValueBuilder;
import java.util.Date;

public class DefaultDateInputBuilder implements IInputValueBuilder<Date>{

    @Override
    public IInputValue<Date> build(String value) throws Exception {
        return new InputDateValue(value);
    }
}
