package rulesystem.ruleinput.rulevalue;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RuleInputDateValue extends RuleInputValue implements Serializable {

    private final Date value;
    private final String stringValue;
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

    public RuleInputDateValue (String value) throws Exception {
        stringValue = value;
        this.dataType = RuleInputDataType.DATE;
        this.value = value == null || value.isEmpty() ? null : formatter.parse(value);
    }

    @Override
    public boolean isEmpty() {
        return value == null;
    }

    @Override
    public int compareTo(String o) {
        try {
            Date otherValue = o == null || o.isEmpty() ? null : formatter.parse(o);
            return this.value.compareTo(otherValue);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        
        return 0;
    }

    @Override
    public String getStringValue() {
        return this.stringValue;
    }
}
