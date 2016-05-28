package com.github.kislayverma.rulette.core.ruleinput.value;

public interface IInputValueBuilder<T> {
    IInputValue<T> build(String value) throws Exception;
}
