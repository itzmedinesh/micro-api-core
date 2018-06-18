package com.itzmeds.mac.core.container;

/**
 * @author itzmeds
 *
 */
public interface WebsocketResource {

	/**
	 * @return String - Websocket resource endpoint access path. The path should being with "/"
	 */
	public String getEndpointPath();

}
