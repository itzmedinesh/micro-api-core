package com.itzmeds.mac.core.service;

import java.util.ArrayList;

/**
 * @author itzmeds
 *
 */
@SuppressWarnings("serial")
public class ResourceList extends ArrayList<Class<? extends AbstractResource>> {
	@SafeVarargs
	public ResourceList(Class<? extends AbstractResource>... classes) {
		for (Class<? extends AbstractResource> clazz : classes) {
			super.add(clazz);
		}
	}
}
