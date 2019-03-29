package com.itzmeds.mac.core.container;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.itzmeds.mac.configuration.Configuration;
import com.itzmeds.mac.core.service.AbstractFactory;
import com.itzmeds.mac.core.service.FilterType;
import com.itzmeds.mac.exception.ServiceInitializationException;

/**
 * Application context object which encapsulates the core container capability
 * defining objects.
 * 
 * @author itzmeds
 *
 */
public class ContainerContext {

	private static final String JERSEY_SERVLET_INIT_PARAMS = "jersey.config.server.provider.classnames";

	private static final String JERSEY_CONTENT_LENGTH_BUFFER = "jersey.config.contentLength.buffer";

	/**
	 * Servlet context object
	 */
	private ServletContextHandler servletContext;

	/**
	 * Servlet container object
	 */
	private ServletHolder servletHolder;

	/**
	 * Servlet container enriched with web-sockets capability
	 */
	private ServerContainer websockContainer;

	/**
	 * Spring bean dependency injection container - annotation based
	 */
	private AnnotationConfigWebApplicationContext applicationContext;

	/**
	 * Spring bean dependency injection container - xml config file based
	 */
	private ClassPathXmlApplicationContext adapterContext;

	private String swaggerAssetsBase;

	/**
	 * Application context constructor
	 * 
	 * @param servletContext
	 *            - static and dynamic resource handler context obejct
	 * @param servletHolder
	 *            - jersey servlet container
	 * @param websockContainer
	 *            - websocket container
	 * @param applicationContext
	 *            - spring annotation based application context
	 * @param adapterContext
	 *            - spring integration adapter context
	 */
	public ContainerContext(ServletContextHandler servletContext, ServletHolder servletHolder,
			ServerContainer websockContainer, AnnotationConfigWebApplicationContext applicationContext,
			ClassPathXmlApplicationContext adapterContext) {
		this.servletContext = servletContext;
		this.servletHolder = servletHolder;
		this.websockContainer = websockContainer;
		this.applicationContext = applicationContext;
		this.adapterContext = adapterContext;
	}

	/**
	 * @return Spring bean dependency injection container - xml config file based
	 */
	public ClassPathXmlApplicationContext getAdapterContext() {
		return this.adapterContext;
	}

	/**
	 * @return Spring bean dependency injection container - annotation based
	 */
	public AnnotationConfigWebApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

	/**
	 * @return Servlet container enriched with web-sockets capability
	 */
	public ServerContainer getWebsocketContext() {
		return this.websockContainer;
	}

	/**
	 * Register servlet filter with the servlet context for the given url pattern
	 * 
	 * @param filterType
	 *            - list of class names implementing servlet filter
	 * @throws ServiceInitializationException
	 *             if filter class is not registered with the spring DI container
	 */
	public void registerWebResourceFilter(FilterType<? extends Filter> filterType)
			throws ServiceInitializationException {
		try {
			Filter filter = applicationContext.getBean(filterType.getFilterClass());

			FilterHolder filterConfigurator = new FilterHolder(filter);
			this.servletContext.addFilter(filterConfigurator, filterType.getFilterPath(),
					EnumSet.of(DispatcherType.REQUEST));
		} catch (Throwable e) {
			throw new ServiceInitializationException(e);
		}
	}

	/**
	 * Add websocket resource class names to the initialized websocket container of
	 * the servlet context
	 * 
	 * @param websocketHandler
	 *            - list of websocket resource controller class names
	 * @throws ServiceInitializationException
	 *             if websocket resource class is not registered with the spring DI
	 *             container
	 */
	public void registerWebsocketHandler(Class<? extends WebsocketResource> websocketHandler)
			throws ServiceInitializationException {

		WebsocketResource websockres = applicationContext.getBean(websocketHandler);

		ServerEndpointConfig config = ServerEndpointConfig.Builder
				.create(websocketHandler, websockres.getEndpointPath()).configurator(new Configurator() {
					@Override
					public <M> M getEndpointInstance(Class<M> endpointClass) throws InstantiationException {
						return endpointClass.cast(websockres);
					}
				}).build();

		try {
			this.websockContainer.addEndpoint(config);
		} catch (DeploymentException e) {
			throw new ServiceInitializationException(e);
		}

	}

	/**
	 * Add jersey resource classes to the jersey servlet container
	 * 
	 * @param restResource
	 *            - REST resource controller class name
	 * @throws ServiceInitializationException
	 *             if class name provided is empty or null
	 */
	public void registerResource(@SuppressWarnings("rawtypes") Class restResource)
			throws ServiceInitializationException {
		Set<String> resourceClassNames = new HashSet<String>();
		if (servletHolder.getInitParameter(JERSEY_SERVLET_INIT_PARAMS) != null) {
			resourceClassNames.addAll(
					Arrays.asList(StringUtils.split(servletHolder.getInitParameter(JERSEY_SERVLET_INIT_PARAMS), ",")));
		}

		if (restResource == null) {
			throw new ServiceInitializationException("Cannot add null or empty resources...");
		}

		resourceClassNames.add(restResource.getCanonicalName());

		servletHolder.setInitParameter(JERSEY_SERVLET_INIT_PARAMS, StringUtils.join(resourceClassNames, ","));
		servletHolder.setInitParameter(JERSEY_CONTENT_LENGTH_BUFFER, "0");
		servletContext.setAttribute(WebApplicationContext.class.getName() + ".ROOT", applicationContext);
	}

	/**
	 * Register spring configuration classes with the spring di container
	 * 
	 * @param configClassName
	 *            - spring config class names
	 * @throws ServiceInitializationException
	 *             if class name provided is empty or null
	 */
	public void registerService(Class<? extends AbstractFactory<? extends Configuration>> configClassName)
			throws ServiceInitializationException {

		if (configClassName == null) {
			throw new ServiceInitializationException("Cannot add null or empty dependencies...");
		}

		applicationContext.register(configClassName);

	}

	/**
	 * 
	 * @param swaggerAssetsBase
	 *            - swagger url path
	 */
	public void setSwaggerAssets(String swaggerAssetsBase) {
		this.swaggerAssetsBase = swaggerAssetsBase;
	}

	/**
	 * @return swagger url path
	 */
	public String getSwaggerAssets() {
		return this.swaggerAssetsBase;
	}

}
