package com.github.kislayverma.rulette.core.ruleinput.value.defaults.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;
import com.github.kislayverma.rulette.core.ruleinput.value.defaults.DefaultBooleanInputBuilder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DefaultBooleanInputBuilderTest {

    @Test
    @DisplayName("should return false for a random string")
    void shouldReturnFalseForRandomValue() {
        IInputValue<Boolean> ivb = new DefaultBooleanInputBuilder().build("xnanannkk");
        assertEquals(Boolean.FALSE, ivb.getValue(), "Random String values are treated as false");
    }

    @Test
    @DisplayName("should return true for a String TRUE value")
    void shouldReturnTrueForTRUEValue() {
        IInputValue<Boolean> ivb = new DefaultBooleanInputBuilder().build("true");
        assertEquals(Boolean.TRUE, ivb.getValue(), "A String True Value treated as true");
    }

}