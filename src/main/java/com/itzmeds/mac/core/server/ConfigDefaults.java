package com.itzmeds.mac.core.server;

/**
 * Default server connector configuration values
 * 
 * @author itzmeds
 *
 */
public interface ConfigDefaults {

	/**
	 * Number of http connector request acceptor thread
	 */
	Integer CONNECTOR_ACCEPTOR_THREADS = 1;

	/**
	 * Number of http connector request selector threads
	 */
	Integer CONNECTOR_SELECTOR_THREADS = 2;

	/**
	 * HTTP request accept queue size
	 */
	Integer CONNECTOR_ACCEPT_QUEUE_SIZE = 50;

	/**
	 * Maximum number of threads to start in the thread pool
	 */
	Integer SERVER_MAX_THREADS = 10;

	/**
	 * Minimum number of threads to start in the thread pool
	 */
	Integer SERVER_MIN_THREADS = 1;

	/**
	 * Thread pool request queue size
	 */
	Integer SERVER_MAX_QUEUED_REQUESTS = 50;

	/**
	 * Idle thread timeout duration
	 */
	Integer SERVER_IDLE_THREAD_TIMEOUT = 5000;

	/**
	 * Websocket client request timeout duration
	 */
	Integer WEBSOCKET_TIMEOUT = 10000;
}
