package com.itzmeds.mac.core.service;

import java.util.ArrayList;

import com.itzmeds.mac.configuration.Configuration;

/**
 * @author itzmeds
 *
 */
@SuppressWarnings("serial")
public class ResourceList extends ArrayList<Class<? extends AbstractResource<? extends Configuration>>> {
	@SafeVarargs
	public ResourceList(Class<? extends AbstractResource<? extends Configuration>>... classes) {
		for (Class<? extends AbstractResource<? extends Configuration>> clazz : classes) {
			super.add(clazz);
		}
	}
}
