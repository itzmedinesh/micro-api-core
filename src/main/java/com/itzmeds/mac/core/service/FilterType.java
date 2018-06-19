package com.itzmeds.mac.core.service;

import javax.servlet.Filter;

/**
 * @author itzmeds
 *
 *         Class to define request filters based on resource access url pattern
 * @param <T>
 *            application configuration class type
 */
public class FilterType<T extends Filter> {

	private Class<T> filterClass;

	private String filterPath;

	public FilterType(Class<T> filterClass, String filterPath) {
		this.filterClass = filterClass;
		this.filterPath = filterPath;
	}

	public Class<T> getFilterClass() {
		return filterClass;
	}

	public String getFilterPath() {
		return filterPath;
	}

}
