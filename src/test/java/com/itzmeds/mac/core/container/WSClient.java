package com.itzmeds.mac.core.container;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

public class WSClient extends Endpoint {

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		System.out.println("Session opened");

		session.addMessageHandler(new MessageHandler.Whole<String>() {

			@Override
			public void onMessage(String msg) {
				System.out.println("Received event: " + msg);

			}

		});

	}
}