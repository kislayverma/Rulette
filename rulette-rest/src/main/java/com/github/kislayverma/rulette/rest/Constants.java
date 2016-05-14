package com.github.kislayverma.rulette.rest;

public class Constants
{
	/**
	 * These define the URL parmaeters used in the route definition strings (e.g. '{userId}').
	 */
	public class Url
	{
		//TODO: Your URL parameter names here...
		public static final String RULE_SYSTEM_NAME = "ruleSystemName";
		public static final String RULE_ID = "ruleId";
	}

	/**
	 * These define the route names used in naming each route definitions.  These names are used
	 * to retrieve URL patterns within the controllers by name to create links in responses.
	 */
	public class Routes
	{
		//TODO: Your Route names here...
		public static final String SINGLE_SAMPLE = "sample.single.route";
		public static final String SAMPLE_COLLECTION = "sample.collection.route";
	}
}
