package com.itzmeds.mac.core.server;

import java.util.concurrent.ArrayBlockingQueue;

import javax.servlet.ServletException;
import javax.websocket.DeploymentException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.itzmeds.mac.configuration.Configuration;
import com.itzmeds.mac.core.container.ContainerContext;

/**
 * Class that adds capabilities to the vanilla version of the server to handle
 * static and dynamic request-response using servlets over http protocol.
 * Configures spring based dependency injection for the servlets and configures
 * handlers to manage REST and Websocket resources.
 * 
 * @author itzmeds
 *
 */
public abstract class AbstractServer<T extends Configuration> implements ConfigDefaults {

	private static final Logger LOGGER = LogManager.getLogger(AbstractServer.class);

	private ServletContextHandler servletContext;

	private ServletHolder servletHolder;

	private AnnotationConfigWebApplicationContext applicationContext;

	private ClassPathXmlApplicationContext adapterContext;

	private ServerContainer websockContainer;

	protected Server server;

	/**
	 * Method to create web resource handlers, DI container and add the same to the
	 * vanilla version of the server
	 * 
	 * @param containerConfig
	 *            - application configuration object
	 * @return application context object
	 * @throws Exception
	 *             if server handler initialization fails
	 */
	protected ContainerContext init(T containerConfig) throws Exception {

		Integer serverMaxThreads = containerConfig.getServerConfig().getThreadPool() != null
				&& containerConfig.getServerConfig().getThreadPool().getMaxThreads() != null
						? containerConfig.getServerConfig().getThreadPool().getMaxThreads()
						: SERVER_MAX_THREADS;

		Integer serverMinThreads = containerConfig.getServerConfig().getThreadPool() != null
				&& containerConfig.getServerConfig().getThreadPool().getMinThreads() != null
						? containerConfig.getServerConfig().getThreadPool().getMinThreads()
						: SERVER_MIN_THREADS;

		Integer serverIdleThreadTimeout = containerConfig.getServerConfig().getThreadPool() != null
				&& containerConfig.getServerConfig().getThreadPool().getIdleThreadTimeout() != null
						? containerConfig.getServerConfig().getThreadPool().getIdleThreadTimeout()
						: SERVER_IDLE_THREAD_TIMEOUT;

		Integer serverMaxQueuedRequests = containerConfig.getServerConfig().getThreadPool() != null
				&& containerConfig.getServerConfig().getThreadPool().getMaxQueuedRequests() != null
						? containerConfig.getServerConfig().getThreadPool().getMaxQueuedRequests()
						: SERVER_MAX_QUEUED_REQUESTS;

		server = new Server(new QueuedThreadPool(serverMaxThreads, serverMinThreads, serverIdleThreadTimeout,
				new ArrayBlockingQueue<>(serverMaxQueuedRequests)));

		configureServerConnectors(containerConfig);
		configureStaticResourceHandler(containerConfig);
		configureRestResourceHandler(containerConfig);

		if (containerConfig.getServerConfig().getWebsocketHandler() != null
				&& containerConfig.getServerConfig().getWebsocketHandler().isEnabled()) {
			configureWebsocketHandler(containerConfig);
		}

		configureResourceAdapters(containerConfig);

		return new ContainerContext(servletContext, servletHolder, websockContainer, applicationContext,
				adapterContext);
	}

	/**
	 * Method to add web-socket resource handling capability to the server
	 * 
	 * @param containerConfig
	 *            - application configuration object
	 * @throws ServletException
	 * @throws DeploymentException
	 */
	private void configureWebsocketHandler(T containerConfig) throws ServletException, DeploymentException {
		LOGGER.info("Configuring websocket handler...");

		Integer webSocketTimeout = containerConfig.getServerConfig().getWebsocketHandler() != null
				&& containerConfig.getServerConfig().getWebsocketHandler().getTimeout() != null
						? Integer.parseInt(containerConfig.getServerConfig().getWebsocketHandler().getTimeout())
						: WEBSOCKET_TIMEOUT;

		websockContainer = WebSocketServerContainerInitializer.configureContext(this.servletContext);
		websockContainer.setDefaultMaxSessionIdleTimeout(webSocketTimeout);
	}

	/**
	 * Method to configure HTTP connector and add the same to the server
	 * 
	 * @param containerConfig
	 *            - applicatoin configuraiton object
	 */
	private void configureServerConnectors(T containerConfig) {

		Integer connectorAcceptorThreads = containerConfig.getServerConfig().getAppConnector()
				.getAcceptorThreads() != null ? containerConfig.getServerConfig().getAppConnector().getAcceptorThreads()
						: CONNECTOR_ACCEPTOR_THREADS;

		Integer connectorSelectorThreads = containerConfig.getServerConfig().getAppConnector()
				.getSelectorThreads() != null ? containerConfig.getServerConfig().getAppConnector().getSelectorThreads()
						: CONNECTOR_SELECTOR_THREADS;

		Integer connectorAcceptQueueSize = containerConfig.getServerConfig().getAppConnector()
				.getAcceptQueueSize() != null ? containerConfig.getServerConfig().getAppConnector().getAcceptQueueSize()
						: CONNECTOR_ACCEPT_QUEUE_SIZE;

		ServerConnector connector = new ServerConnector(server, connectorAcceptorThreads, connectorSelectorThreads);
		connector.setPort(containerConfig.getServerConfig().getAppConnector().getPort());

		connector.setAcceptQueueSize(connectorAcceptQueueSize);

		server.setConnectors(new Connector[] { connector });
	}

	/**
	 * Method to configure static resource serving HTTP handlers for the server
	 * 
	 * @param containerConfig
	 *            - application configuration object
	 */
	private void configureStaticResourceHandler(T containerConfig) {
		LOGGER.info("Configuring server static resource handlers...");
		ResourceHandler resHandler = new ResourceHandler();
		resHandler.setDirectoriesListed(false);
		resHandler.setResourceBase(containerConfig.getServerConfig().getResourceHandler().getResourceBase());

		ContextHandler ctxHandler = new ContextHandler(
				containerConfig.getServerConfig().getResourceHandler().getResourceCtx());
		ctxHandler.setHandler(resHandler);

		HandlerList handlers = new HandlerList();

		handlers.setHandlers(new Handler[] { ctxHandler });
		server.setHandler(handlers);
		LOGGER.info("Server static resource handlers configured successfully!");
	}

	/**
	 * Method to configure dynamic resource serving HTTP handlers for the server
	 * 
	 * @param containerConfig
	 *            - application configuration object
	 */
	private void configureRestResourceHandler(T containerConfig) {
		LOGGER.info("Configuring REST resource handlers...");
		ServletContextHandler restHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		restHandler.setContextPath(containerConfig.getServerConfig().getResourceHandler().getRestApiCtx());
		ServletHolder jerseyServlet = restHandler.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		jerseyServlet.setInitOrder(0);

		HandlerList handlers = new HandlerList();

		for (Handler handler : server.getHandlers()) {
			handlers.addHandler(handler);
		}

		handlers.addHandler(restHandler);
		server.setHandler(handlers);

		this.servletContext = restHandler;
		this.servletHolder = jerseyServlet;
		LOGGER.info("Server REST handlers configured successfully!");
	}

	/**
	 * Method to configure spring based dependency injection container for the
	 * server. Configures both annotation based and XML based spring DI
	 * 
	 * @param containerConfig
	 *            - application configuration object
	 */
	private void configureResourceAdapters(T containerConfig) {
		LOGGER.info("Configuring server resource adapters...");
		GenericApplicationContext parent = new StaticApplicationContext();
		parent.getBeanFactory().registerSingleton("configuration", containerConfig);
		parent.refresh();

		AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
		applicationContext.setParent(parent);

		this.applicationContext = applicationContext;

		if (containerConfig.getServerConfig().getResourceAdapterConfig() != null) {
			ClassPathXmlApplicationContext adapterContext = new ClassPathXmlApplicationContext(
					new String[] { containerConfig.getServerConfig().getResourceAdapterConfig() }, false);
			adapterContext.setParent(applicationContext);

			this.adapterContext = adapterContext;
		}

		LOGGER.info("Server resource adapters configured successfully!");
	}

}
