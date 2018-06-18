package com.itzmeds.mac.core.container;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.itzmeds.mac.core.server.TestConfiguration;
import com.itzmeds.mac.core.server.TestSpringAdapterBean;
import com.itzmeds.mac.core.service.AbstractFactory;

@Configuration
public class TestFactoryImpl extends AbstractFactory<TestConfiguration> {

	@Bean
	TestSpringAdapterBean testSpringBean() {
		return new TestSpringAdapterBean();
	}

	@Bean
	TestWebsocketResource testWebsockResource() {
		return new TestWebsocketResource();
	}

	@Bean
	TestFilter testFilter() {
		return new TestFilter();
	}

}
