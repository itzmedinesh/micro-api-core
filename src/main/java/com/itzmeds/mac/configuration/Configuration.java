package com.itzmeds.mac.configuration;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Application server base configuration object
 * 
 * @author itzmeds
 *
 */
public class Configuration {

	/**
	 * Application server instance configuration values (specific to the business)
	 */
	@JsonProperty("service")
	@NotNull
	private ServiceConfig serviceConfig;

	/**
	 * Application server configuration parameters
	 */
	@JsonProperty("server")
	@NotNull
	private ServerConfig serverConfig;

	public ServiceConfig getServiceConfig() {
		return serviceConfig;
	}

	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	@Override
	public String toString() {
		return "Configuration [serviceConfig=" + serviceConfig + ", serverConfig=" + serverConfig + "]";
	}

}