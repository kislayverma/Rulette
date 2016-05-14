package com.github.kislayverma.rulette.core.validator;

import com.github.kislayverma.rulette.core.rule.Rule;

public class DefaultValidator implements Validator{
	@Override
    public boolean isValid(Rule rule) {
    	return true;
    }
}
