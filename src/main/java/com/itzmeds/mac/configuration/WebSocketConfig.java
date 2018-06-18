package com.itzmeds.mac.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Websocket configuration object
 * 
 * @author itzmeds
 *
 */
public class WebSocketConfig {

	/**
	 * Should websocket capability be enabled?
	 */
	@JsonProperty("enabled")
	private boolean enabled;

	/**
	 * Websocket client session timeout duration
	 */
	@JsonProperty("timeout")
	private String timeout;

	public boolean isEnabled() {
		return enabled;
	}

	public String getTimeout() {
		return timeout;
	}

	@Override
	public String toString() {
		return "WebSocketConfig [enabled=" + enabled + ", timeout=" + timeout + "]";
	}

}
