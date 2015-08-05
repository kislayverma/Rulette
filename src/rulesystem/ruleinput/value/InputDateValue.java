package rulesystem.ruleinput.value;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InputDateValue extends RuleInputValue implements IInputValue<Date>, Serializable {

    private final Date value;
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

    public InputDateValue (String value) throws Exception {
        this.dataType = InputDataType.DATE;
        this.value = value == null || value.isEmpty() ? null : formatter.parse(value);
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
        Date otherValue = formatter.parse(obj);
        return this.value.compareTo(otherValue);
    }
}
