package com.itzmeds.mac.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * HTTP thread pool configuration object
 * 
 * @author itzmeds
 *
 */
public class ThreadPoolConfig {

	/**
	 * Max number of threads in the pool
	 */
	@JsonProperty("maxThreads")
	private Integer maxThreads;

	/**
	 * Min number of threads in the pool
	 */
	@JsonProperty("minThreads")
	private Integer minThreads;

	/**
	 * Max queued requests in the pool
	 */
	@JsonProperty("maxQueuedRequests")
	private Integer maxQueuedRequests;

	/**
	 * Idle thread timeout duration
	 */
	@JsonProperty("idleThreadTimeout")
	private Integer idleThreadTimeout;

	public Integer getMaxThreads() {
		return maxThreads;
	}

	public Integer getMinThreads() {
		return minThreads;
	}

	public Integer getMaxQueuedRequests() {
		return maxQueuedRequests;
	}

	public Integer getIdleThreadTimeout() {
		return idleThreadTimeout;
	}

	@Override
	public String toString() {
		return "ThreadPoolConfig [maxThreads=" + maxThreads + ", minThreads=" + minThreads + ", maxQueuedRequests="
				+ maxQueuedRequests + ", idleThreadTimeout=" + idleThreadTimeout + "]";
	}

}
