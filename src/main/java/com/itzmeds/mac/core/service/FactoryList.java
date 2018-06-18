package com.itzmeds.mac.core.service;

import java.util.ArrayList;

import com.itzmeds.mac.configuration.Configuration;

/**
 * @author itzmeds
 *
 */
@SuppressWarnings("serial")
public class FactoryList extends ArrayList<Class<? extends AbstractFactory<? extends Configuration>>> {
	@SafeVarargs
	public FactoryList(Class<? extends AbstractFactory<? extends Configuration>>... classes) {
		for (Class<? extends AbstractFactory<? extends Configuration>> clazz : classes) {
			super.add(clazz);
		}
	}
}