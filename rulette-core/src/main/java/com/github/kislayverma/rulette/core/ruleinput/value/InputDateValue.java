package com.github.kislayverma.rulette.core.ruleinput.value;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class InputDateValue extends RuleInputValue implements IInputValue<Date>, Serializable {

    private final Date value;
    private final DateTimeFormatter formatter;

    public InputDateValue (String value) throws Exception {
        this(value, DateTimeFormat.forPattern("yyyyMMdd"));
    }

    public InputDateValue (String value, DateTimeFormatter formatter) throws Exception {
        this.dataType = InputDataType.DATE;
        this.formatter = formatter;
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
    public int compareTo(String obj) throws ParseException {
        return this.value.compareTo(formatter.parseDateTime(obj).toDate());
    }
}
