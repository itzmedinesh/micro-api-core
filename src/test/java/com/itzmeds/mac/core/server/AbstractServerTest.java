package com.itzmeds.mac.core.server;

import static org.junit.Assert.fail;

import java.io.File;

import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.itzmeds.mac.core.container.ContainerContext;

public class AbstractServerTest extends AbstractServer<TestConfiguration> {

	private static ObjectMapper yamlParser = new ObjectMapper(new YAMLFactory());

	private static TestConfiguration testConfiguration;

	@BeforeClass
	public static void setUp() throws Exception {
		testConfiguration = yamlParser.readValue(new File("test.yml"), TestConfiguration.class);
	}

	@Test
	public void testInit() {
		try {
			ContainerContext containerContext = init(testConfiguration);

			Assert.assertNotNull(containerContext);

			long timeout = containerContext.getWebsocketContext().getDefaultMaxSessionIdleTimeout();

			containerContext.getApplicationContext().refresh();

			TestConfiguration testconfig = containerContext.getApplicationContext().getBean(TestConfiguration.class);

			Assert.assertEquals("" + testconfig.getServerConfig().getAppConnector().getAcceptorThreads(), "1");

			containerContext.getAdapterContext().refresh();

			TestSpringAdapterBean testBean = containerContext.getAdapterContext().getBean(TestSpringAdapterBean.class);

			Assert.assertEquals(testBean.testSpringBeanName, "TestSpringAdapterBean");

			Assert.assertEquals(timeout, 0);

			Assert.assertTrue(server.getHandlers().length > 0);

			HandlerList handlerList = (HandlerList) server.getHandlers()[0];

			ContextHandler serverCtxHandler = handlerList.getChildHandlerByClass(ContextHandler.class);

			Assert.assertEquals("/docs", serverCtxHandler.getContextPath());

			ServletContextHandler restCtxHanlder = handlerList.getChildHandlerByClass(ServletContextHandler.class);

			Assert.assertEquals("/api", restCtxHanlder.getContextPath());

		} catch (Exception e) {
			fail("Not expected..." + e.toString());
		}

	}

}
