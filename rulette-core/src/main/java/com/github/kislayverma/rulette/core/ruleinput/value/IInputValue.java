package com.github.kislayverma.rulette.core.ruleinput.value;

public interface IInputValue<T> {
    String getDataType();
    T getValue();
    int compareTo(String obj) throws Exception;
    int compareTo(IInputValue<T> obj);
    boolean isEmpty();
}
