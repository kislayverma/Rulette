package com.github.kislayverma.rulette.core.ruleinput.value.defaults;

import com.github.kislayverma.rulette.core.ruleinput.value.IInputValueBuilder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBuilderRegistry {
    private final Map<String, IInputValueBuilder> builderRegister;

    public DefaultBuilderRegistry() {
        System.out.println("Initializing default builder registry...");
        this.builderRegister = new ConcurrentHashMap<>();
        this.builderRegister.put("STRING", new DefaultStringInputBuilder());
        this.builderRegister.put("DATE", new DefaultDateInputBuilder());
        this.builderRegister.put("NUMBER", new DefaultNumberInputBuilder());
    }

    public IInputValueBuilder getDefaultBuilder(String dataType) {
        return this.builderRegister.get(dataType);
    }
}
