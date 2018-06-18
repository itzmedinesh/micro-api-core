package com.itzmeds.mac.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * HTTP server configuration object
 * 
 * @author itzmeds
 *
 */
public class ServerConfig {

	/**
	 * Http connector configuration object
	 */
	@JsonProperty("appConnector")
	private HttpConnectorConfig appConnector;

	/**
	 * HTTP thread pool configuration object
	 */
	@JsonProperty("threadPool")
	private ThreadPoolConfig threadPool;

	/**
	 * Web-sockets configuration object
	 */
	@JsonProperty("websocketHandler")
	private WebSocketConfig websocketHandler;

	/**
	 * Server resource handler configuration object
	 */
	@JsonProperty("resourceHandler")
	private ResourceHandler resourceHandler;

	/**
	 * Spring xml based DI configuration file path
	 */
	@JsonProperty("resourceAdapterConfig")
	private String resourceAdapterConfig;

	public HttpConnectorConfig getAppConnector() {
		return appConnector;
	}

	public ThreadPoolConfig getThreadPool() {
		return threadPool;
	}

	public ResourceHandler getResourceHandler() {
		return resourceHandler;
	}

	public WebSocketConfig getWebsocketHandler() {
		return websocketHandler;
	}

	public String getResourceAdapterConfig() {
		return resourceAdapterConfig;
	}

	@Override
	public String toString() {
		return "ServerConfig [appConnector=" + appConnector + ", threadPool=" + threadPool + ", websocketHandler="
				+ websocketHandler + ", resourceHandler=" + resourceHandler + ", resourceAdapterConfig="
				+ resourceAdapterConfig + "]";
	}

}