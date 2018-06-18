package com.itzmeds.mac.core.service;

import javax.servlet.Filter;

/**
 * @author itzmeds
 *
 * @param <T>
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
