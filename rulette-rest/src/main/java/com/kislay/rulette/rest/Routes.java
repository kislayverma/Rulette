package com.kislay.rulette.rest;

import io.netty.handler.codec.http.HttpMethod;

import org.restexpress.RestExpress;

public abstract class Routes
{
	public static void define(Configuration config, RestExpress server)
    {
        // Get all rules in a rule system
		server.uri("/getAllRules/{ruleSystemName}.{format}", config.getRuleSystemController())
			.action("getAllRules", HttpMethod.GET)
			.name(Constants.Routes.SAMPLE_COLLECTION);

        server.uri("/addRule/{ruleSystemName}.{format}", config.getSampleController())
			.method(HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
			.name(Constants.Routes.SINGLE_SAMPLE);

		server.uri("/your/route/here.{format}", config.getSampleController())
			.action("readAll", HttpMethod.GET)
			.method(HttpMethod.POST)
			.name(Constants.Routes.SAMPLE_COLLECTION);
// or...
//		server.regex("/some.regex", config.getRouteController());
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
