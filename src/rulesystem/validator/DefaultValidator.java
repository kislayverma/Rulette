package rulesystem.validator;

import rulesystem.rule.Rule;

public class DefaultValidator implements Validator{
	@Override
    public boolean isValid(Rule rule) {
    	return true;
    }
}
