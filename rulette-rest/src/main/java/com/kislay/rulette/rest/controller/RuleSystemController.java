package com.kislay.rulette.rest.controller;

import com.kislay.rulette.RuleSystem;
import com.kislay.rulette.rule.Rule;
import java.util.Collections;
import java.util.List;

import org.restexpress.Request;
import org.restexpress.Response;

public class RuleSystemController {
    private static RuleSystem RULE_SYSTEM;

    public RuleSystemController() throws Exception {
        if (RULE_SYSTEM == null) {
            RULE_SYSTEM = new RuleSystem("discount_rule_system", null);
        }
    }

	public List<Rule> getAllRules(Request request, Response response) {
		return RULE_SYSTEM.getAllRules();
	}

    public Object create(Request request, Response response) {
		//TODO: Your 'POST' logic here...
		return null;
	}

	public Object read(Request request, Response response) {
		//TODO: Your 'GET' logic here...
		return null;
	}

	public List<Object> readAll(Request request, Response response) {
		//TODO: Your 'GET collection' logic here...
		return Collections.emptyList();
	}

	public void update(Request request, Response response) {
		//TODO: Your 'PUT' logic here...
		response.setResponseNoContent();
	}

	public void delete(Request request, Response response) {
		//TODO: Your 'DELETE' logic here...
		response.setResponseNoContent();
	}
}
