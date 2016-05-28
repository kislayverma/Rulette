package com.github.kislayverma.rulette.core.ruleinput.value.defaults;

import com.github.kislayverma.rulette.core.ruleinput.value.IInputValueBuilder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultBuilderRegistry {
    private final Map<String, IInputValueBuilder> builderRegister;
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBuilderRegistry.class);

    public DefaultBuilderRegistry() {
        LOGGER.info("Initializing default builder registry...");

        this.builderRegister = new ConcurrentHashMap<>();
        this.builderRegister.put("STRING", new DefaultStringInputBuilder());
        this.builderRegister.put("DATE", new DefaultDateInputBuilder());
        this.builderRegister.put("NUMBER", new DefaultNumberInputBuilder());
    }

    public IInputValueBuilder getDefaultBuilder(String dataType) {
        return this.builderRegister.get(dataType);
    }
}
