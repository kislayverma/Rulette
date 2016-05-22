package com.github.kislayverma.rulette.core.ruleinput.value;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InputDataTypeFactory {
    private static InputDataTypeFactory INSTANCE;
    private static final Map<String, Class> handlerMap = new ConcurrentHashMap<>();

    private InputDataTypeFactory() {
        System.out.println("Input data type factory initialized");
    }

    public static InputDataTypeFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InputDataTypeFactory();
        }

        return INSTANCE;
    }

    public static IInputValue getHandler(String dataType) {
        return handlerMap.get(dataType);
    }

    public static void registerDataType(IInputValue dataType) {
        IInputValue handler = handlerMap.put(dataType.getDataType(), dataType.getClass());
    }
}
