package com.itzmeds.mac.core.service;

import java.util.ArrayList;

import com.itzmeds.mac.core.container.WebsocketResource;

/**
 * @author itzmeds
 *
 */
@SuppressWarnings("serial")
public class WebsocketResourceList extends ArrayList<Class<? extends WebsocketResource>> {
	@SafeVarargs
	public WebsocketResourceList(Class<? extends WebsocketResource>... classes) {
		for (Class<? extends WebsocketResource> clazz : classes) {
			super.add(clazz);
		}
	}
}
