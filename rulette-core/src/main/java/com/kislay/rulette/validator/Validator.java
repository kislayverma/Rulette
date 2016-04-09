package com.kislay.rulette.validator;

import com.kislay.rulette.rule.Rule;

public interface Validator {
	boolean isValid(Rule rule);
}
