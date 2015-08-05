package rulesystem.validator;

import rulesystem.rule.Rule;

public interface Validator {
	boolean isValid(Rule rule);
}
