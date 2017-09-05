package com.github.kislayverma.rulette.core.ruleinput.value.defaults;

import java.text.ParseException;

/**
 * Created by 16545 on 27/08/17.
 */
public class CustomInputDateValue extends InputDateValue {
    public CustomInputDateValue(String value) throws Exception {
        this.value = (value == null || value.isEmpty()) ? null : formatter.parseDateTime(value.substring(0, 19)).toDate(); //Taking till seconds value of the date string.
    }

    @Override
    public int compareTo(String obj) throws ParseException {
        return this.value.compareTo(formatter.parseDateTime(obj.substring(0, 19)).toDate());
    }
}
