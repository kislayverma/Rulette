package com.github.kislayverma.rulette.rest;

import io.netty.handler.codec.http.HttpMethod;

import org.restexpress.RestExpress;

public abstract class Routes {
	public static void define(Configuration config, RestExpress server) {
        // Get all rules in a rule system
		server.uri("/getAllRules/{ruleSystemName}.{format}", config.getRuleSystemController())
			.action("getAllRules", HttpMethod.GET)
			.name(Constants.Routes.SAMPLE_COLLECTION);

        server.uri("/getApplicableRule/{ruleSystemName}.{format}", config.getRuleSystemController())
			.action("getApplicableRule", HttpMethod.POST)
			.name(Constants.Routes.SINGLE_SAMPLE);

        server.uri("/getNextApplicableRule/{ruleSystemName}.{format}", config.getRuleSystemController())
			.action("getNextApplicableRule", HttpMethod.POST)
			.name(Constants.Routes.SINGLE_SAMPLE);

        server.uri("/getRule/{ruleSystemName}/{ruleId}.{format}", config.getRuleSystemController())
			.action("getRule", HttpMethod.GET)
			.name(Constants.Routes.SINGLE_SAMPLE);

        server.uri("/addRule/{ruleSystemName}.{format}", config.getRuleSystemController())
			.action("addRule", HttpMethod.POST)
			.name(Constants.Routes.SINGLE_SAMPLE);

        server.uri("/updateRule/{ruleSystemName}/{ruleId}.{format}", config.getRuleSystemController())
			.action("updateRule", HttpMethod.PUT)
			.name(Constants.Routes.SINGLE_SAMPLE);

        server.uri("/deleteRule/{ruleSystemName}/{ruleId}.{format}", config.getRuleSystemController())
			.action("deleteRule", HttpMethod.DELETE)
			.name(Constants.Routes.SINGLE_SAMPLE);

        server.uri("/reload/{ruleSystemName}.{format}", config.getRuleSystemController())
			.action("reloadRuleSystem", HttpMethod.PUT)
			.name(Constants.Routes.SINGLE_SAMPLE);
    }

//	public static void define(Configuration config, RestExpress server)
//    {
//		//TODO: Your routes here...
//		server.uri("/your/route/here/{sampleId}.{format}", config.getSampleController())
//			.method(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE)
//			.name(Constants.Routes.SINGLE_SAMPLE);
//
//		server.uri("/your/route/here.{format}", config.getSampleController())
//			.action("readAll", HttpMethod.GET)
//			.method(HttpMethod.POST)
//			.name(Constants.Routes.SAMPLE_COLLECTION);
//// or...
////		server.regex("/some.regex", config.getRouteController());
//    }
}
