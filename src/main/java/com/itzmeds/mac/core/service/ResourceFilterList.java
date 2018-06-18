package com.itzmeds.mac.core.service;

import java.util.LinkedList;

import javax.servlet.Filter;

/**
 * @author itzmeds
 *
 */
@SuppressWarnings("serial")
public class ResourceFilterList extends LinkedList<FilterType<? extends Filter>> {
	@SafeVarargs
	public ResourceFilterList(FilterType<? extends Filter>... filters) {
		for (FilterType<? extends Filter> filter : filters) {
			super.add(filter);
		}
	}
}
