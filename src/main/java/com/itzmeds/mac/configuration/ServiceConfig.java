package com.itzmeds.mac.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Application instance configuration (Specific to the business)
 * 
 * @author itzmeds
 *
 */
public class ServiceConfig {

	/**
	 * Name for the currently running application service instance
	 */
	@JsonProperty("name")
	private String name;

	/**
	 * Version of the currently running application service instances
	 */
	@JsonProperty("version")
	private String version;

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return "ServiceConfig [name=" + name + ", version=" + version + "]";
	}

}