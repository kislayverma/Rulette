package com.kislay.rulette.validator;

import com.kislay.rulette.rule.Rule;

public class DefaultValidator implements Validator{
	@Override
    public boolean isValid(Rule rule) {
    	return true;
    }
}
