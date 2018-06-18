package com.itzmeds.mac.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * HTTP connector configuration object
 * 
 * @author itzmeds
 *
 */
public class HttpConnectorConfig {

	/**
	 * Port on which the webserver will run
	 */
	@JsonProperty("port")
	private Integer port;

	/**
	 * Number of threads to process http request from queue. lesser than or half of
	 * selector threads
	 */
	@JsonProperty("acceptorThreads")
	Integer acceptorThreads;

	/**
	 * HTTP request managing thread. Equals number of CPUs
	 */
	@JsonProperty("selectorThreads")
	Integer selectorThreads;

	/**
	 * HTTP request queue size
	 */
	@JsonProperty("acceptQueueSize")
	Integer acceptQueueSize;

	public Integer getPort() {
		return port;
	}

	public Integer getAcceptorThreads() {
		return acceptorThreads;
	}

	public Integer getSelectorThreads() {
		return selectorThreads;
	}

	public Integer getAcceptQueueSize() {
		return acceptQueueSize;
	}

	@Override
	public String toString() {
		return "HttpConnectorConfig [port=" + port + ", acceptorThreads=" + acceptorThreads + ", selectorThreads="
				+ selectorThreads + ", acceptQueueSize=" + acceptQueueSize + "]";
	}

}
