package com.github.kislayverma.rulette.rest.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kislayverma.rulette.RuleSystem;
import com.github.kislayverma.rulette.rest.Constants;
import com.github.kislayverma.rulette.core.rule.Rule;
import io.netty.handler.codec.http.HttpResponseStatus;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.restexpress.Request;
import org.restexpress.Response;

public class RuleSystemController {
    private final RuleSystemFactory ruleSystemFactory;

    public RuleSystemController() {
        this.ruleSystemFactory = new RuleSystemFactory();
    }

    public List<Rule> getAllRules(Request request, Response response) {
        String ruleSystemName = request.getHeader(Constants.Url.RULE_SYSTEM_NAME);
		return getRuleSystem(ruleSystemName).getAllRules();
	}

	public Rule getApplicableRule(Request request, Response response) throws Exception {
        try {
            Map<String, String> map = convertInputJsonToMap(request);
            String ruleSystemName = request.getHeader(Constants.Url.RULE_SYSTEM_NAME);

            return getRuleSystem(ruleSystemName).getRule(map);
        } catch (IOException ex) {
            ex.printStackTrace();
            response.setException(ex);
            response.setResponseStatus(HttpResponseStatus.BAD_REQUEST);
        }

        return null;
	}

	public Rule getNextApplicableRule(Request request, Response response) throws Exception {
        try {
            Map<String, String> map = convertInputJsonToMap(request);
            String ruleSystemName = request.getHeader(Constants.Url.RULE_SYSTEM_NAME);

            return getRuleSystem(ruleSystemName).getNextApplicableRule(map);
        } catch (IOException ex) {
            ex.printStackTrace();
            response.setException(ex);
            response.setResponseStatus(HttpResponseStatus.BAD_REQUEST);
        }

        return null;
	}

	public Rule getRule(Request request, Response response) {
        String ruleSystemName = request.getHeader(Constants.Url.RULE_SYSTEM_NAME);
        String ruleIdStr = request.getHeader(Constants.Url.RULE_ID);
        Integer ruleId = Integer.parseInt(ruleIdStr);

        return getRuleSystem(ruleSystemName).getRule(ruleId);
	}

    public Rule addRule(Request request, Response response) throws Exception {
        try {
            Map<String, String> map = convertInputJsonToMap(request);
            String ruleSystemName = request.getHeader(Constants.Url.RULE_SYSTEM_NAME);

            return getRuleSystem(ruleSystemName).addRule(map);
        } catch (IOException ex) {
            ex.printStackTrace();
            response.setException(ex);
            response.setResponseStatus(HttpResponseStatus.BAD_REQUEST);
        }

        return null;
	}

	public Rule updateRule(Request request, Response response) throws Exception {
        try {
            Map<String, String> map = convertInputJsonToMap(request);
            String ruleSystemName = request.getHeader(Constants.Url.RULE_SYSTEM_NAME);
            String ruleIdStr = request.getHeader(Constants.Url.RULE_ID);
            Integer ruleId = Integer.parseInt(ruleIdStr);

            RuleSystem rs = getRuleSystem(ruleSystemName);
            Rule oldRule = rs.getRule(ruleId);
            if (oldRule == null) {
                throw new RuntimeException("No existing rule found for the input");
            } else {
                Rule newRule = rs.createRuleObject(map);

                return rs.updateRule(oldRule, newRule);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            response.setException(ex);
            response.setResponseStatus(HttpResponseStatus.BAD_REQUEST);
        }

        return null;
	}

	public void deleteRule(Request request, Response response) throws Exception {
        try {
            Map<String, String> map = convertInputJsonToMap(request);
            String ruleSystemName = request.getHeader(Constants.Url.RULE_SYSTEM_NAME);
            String ruleIdStr = request.getHeader(Constants.Url.RULE_ID);
            Integer ruleId = Integer.parseInt(ruleIdStr);

            getRuleSystem(ruleSystemName).deleteRule(ruleId);
            response.setResponseNoContent();
        } catch (IOException ex) {
            ex.printStackTrace();
            response.setException(ex);
            response.setResponseStatus(HttpResponseStatus.BAD_REQUEST);
        }
	}

	public void reloadRuleSystem(Request request, Response response) throws Exception {
        String ruleSystemName = request.getHeader(Constants.Url.RULE_SYSTEM_NAME);
        ruleSystemFactory.reloadRuleSystem(ruleSystemName);
        response.setResponseNoContent();
	}

    private RuleSystem getRuleSystem(String ruleSystemName) {
        return ruleSystemFactory.getRuleSystem(ruleSystemName);
    }

    private Map<String, String> convertInputJsonToMap(Request request) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
//        System.out.println(request.getHeaderNames());
//        System.out.println(request.getBodyFromUrlFormEncoded(true));

        Map<String, String> map = new HashMap<>();
        Set<String> keySet = request.getBodyFromUrlFormEncoded(true).keySet();
        for (String payload : keySet) {
            Map<String, Object> objMap = mapper.readValue(payload, new TypeReference<Map<String, Object>>(){});
            for (Map.Entry<String, Object> entry : objMap.entrySet()) {
                map.put(entry.getKey(), (String) entry.getValue());
            }

            return map;
        }

        return null;
    }
}
