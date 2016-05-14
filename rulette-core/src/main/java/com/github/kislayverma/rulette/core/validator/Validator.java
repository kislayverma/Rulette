package com.github.kislayverma.rulette.core.validator;

import com.github.kislayverma.rulette.core.rule.Rule;

public interface Validator {
	boolean isValid(Rule rule);
}
