package com.itzmeds.mac.core.container;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

@ServerEndpoint(value = "Test Listing")
public class TestWebsocketResource implements WebsocketResource {

	private static final Logger LOGGER = LogManager.getLogger(TestWebsocketResource.class);

	@OnOpen
	public void onOpen(Session session) {
		LOGGER.info("onOpen called");
	}

	@OnClose
	public void onClose(Session session) {
		LOGGER.info("onClose called");
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		LOGGER.info("onMessage called : client says : " + message);
		session.getAsyncRemote().sendText("Server says : Welcome test list websock client!!!");
	}

	@OnError
	public void onError(Throwable error) {
		LOGGER.info("onError called");
	}

	@Override
	public String getEndpointPath() {
		return "/testlist";
	}

}
