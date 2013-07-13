package rulesystem.validator;

import rulesystem.Rule;

public class DefaultValidator implements Validator{
	@Override
    public boolean isValid(Rule rule) {
    	return true;
    }
}
