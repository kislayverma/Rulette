package com.github.kislayverma.rulette.core.ruleinput.value.defaults;

import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

class InputDateValue implements IInputValue<Date>, Serializable {

    private final Date value;
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public InputDateValue(String value) throws Exception {
        if (value == null || value.isEmpty()) {
            this.value = null;
        } else {
            String timeValueInFormat = value.substring(0, 19);
            this.value = formatter.parseDateTime(timeValueInFormat).toDate();
        }
    }

    @Override
    public boolean isEmpty() {
        return value == null;
    }

    @Override
    public Date getValue() {
        return this.value;
    }

    @Override
    public int compareTo(String obj) throws ParseException {
        return this.value.compareTo(formatter.parseDateTime(obj).toDate());
    }

    @Override
    public String getDataType() {
        return Date.class.getName();
    }
}
