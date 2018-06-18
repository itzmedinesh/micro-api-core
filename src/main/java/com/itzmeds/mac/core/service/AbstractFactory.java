
package com.itzmeds.mac.core.service;

import javax.inject.Inject;
import javax.inject.Named;

import com.itzmeds.mac.configuration.Configuration;

/**
 * @author itzmeds
 *
 * @param <T>
 */
@org.springframework.context.annotation.Configuration
public abstract class AbstractFactory<T extends Configuration> {

	@Inject
	@Named("configuration")
	T configuration;

	protected T getConfiguration() {
		return configuration;
	}

}
