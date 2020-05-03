package com.github.kislayverma.rulette.core.ruleinput.value.defaults;

import com.github.kislayverma.rulette.core.ruleinput.value.IInputValue;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

class InputDateValue implements IInputValue<Date>, Serializable {
    private static final long serialVersionUID = 5666450390675442878L;
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_PATTERN);
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);

    private final Date value;

    public InputDateValue (String value) {
        this.value = value == null || value.isEmpty() ? null : formatter.parseDateTime(value).toDate();
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
    public int compareTo(String obj) {
        return this.value.compareTo(formatter.parseDateTime(obj).toDate());
    }

    @Override
    public String getDataType() {
        return Date.class.getName();
    }

    @Override
    public int compareTo(IInputValue<Date> obj) {
        if (this.isEmpty() && obj.isEmpty()) {
            return 0;
        } else {
            return this.value.compareTo(obj.getValue());
        }
    }

    @Override
    public boolean equals(Object obj) {
        IInputValue<Date> that = (IInputValue<Date>) obj;
        if (this.isEmpty() && that.isEmpty()) {
            return true;
        } else if (!this.isEmpty()) {
            return this.value.equals(that.getValue());
        } else {
            return that.getValue().equals(this.value);
        }
    }

    @Override
    public String toString() {
        return this.value  == null ? "" : SIMPLE_DATE_FORMAT.format(this.value);
    }
}
