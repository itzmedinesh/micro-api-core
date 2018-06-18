package com.itzmeds.mac.core.container;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class TestFilter implements Filter {

	private static final Logger LOGGER = LogManager.getLogger(TestFilter.class);

	public TestFilter() {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		LOGGER.info("From test filter");
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

}
