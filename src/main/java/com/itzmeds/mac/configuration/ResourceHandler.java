package com.itzmeds.mac.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Servlet resource handler configuration class
 * 
 * @author itzmeds
 *
 */
public class ResourceHandler {

	/**
	 * Path where the static web resources are placed
	 */
	@JsonProperty("resourceBase")
	String resourceBase;

	/**
	 * relative url to access the static web resources
	 */
	@JsonProperty("resourceCtx")
	String resourceCtx;

	/**
	 * relative url to access dynamic web resources
	 */
	@JsonProperty("restApiCtx")
	String restApiCtx;

	public String getResourceBase() {
		return resourceBase;
	}

	public void setResourceBase(String resourceBase) {
		this.resourceBase = resourceBase;
	}

	public String getResourceCtx() {
		return resourceCtx;
	}

	public String getRestApiCtx() {
		return restApiCtx;
	}

	@Override
	public String toString() {
		return "ResourceHandler [resourceBase=" + resourceBase + ", resourceCtx=" + resourceCtx + ", restApiCtx="
				+ restApiCtx + "]";
	}

}
