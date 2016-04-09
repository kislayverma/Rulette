package rulette.validator;

import rulette.rule.Rule;

public interface Validator {
	boolean isValid(Rule rule);
}
