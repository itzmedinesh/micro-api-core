package com.itzmeds.mac.core.container;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.servlet.Filter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.itzmeds.mac.configuration.Configuration;
import com.itzmeds.mac.core.server.AbstractServer;
import com.itzmeds.mac.core.server.ServerApi;
import com.itzmeds.mac.core.service.AbstractFactory;
import com.itzmeds.mac.core.service.AbstractResource;
import com.itzmeds.mac.core.service.FilterType;
import com.itzmeds.mac.core.service.ResourceFilterList;
import com.itzmeds.mac.exception.ConfigurationException;
import com.itzmeds.mac.exception.ServiceInitializationException;

import io.swagger.jaxrs.config.BeanConfig;

/**
 * Class to be extended by the application service class to setup the server
 * environment and start the same.
 * 
 * @author itzmeds
 *
 * @param <T>
 *            Application configuration class type
 */
public abstract class AbstractContainer<T extends Configuration> extends AbstractServer<T> implements Container {

	private static final Logger LOGGER = LogManager.getLogger(AbstractContainer.class);

	private static final String CONFIG_FILE_EXTENSION = "yml";
	private static final int TEMP_DIR_ATTEMPTS = 10000;

	/**
	 * @param arguments
	 *            YAML configuration file
	 * @throws Exception
	 */
	protected void boot(String... arguments) throws Exception {

		final T containerCfg = loadConfiguration(arguments);

		preInitialize(containerCfg);

		final ContainerContext containerCtx = init(containerCfg);

		server.addLifeCycleListener(new ContainerLifecycle(containerCtx) {

			@Override
			public void whileStarting() {
				try {
					LOGGER.info("Initializing container components... ");
					initialize(containerCfg, containerCtx);
					registerServices(containerCfg, containerCtx);
					containerCtx.getApplicationContext().refresh();

					registerWebsocketResources(containerCfg, containerCtx);

					registerWebResourceFilters(containerCtx);

					registerResources(containerCfg, containerCtx);
					LOGGER.info("Container components initialized sucessfully!");
				} catch (ServiceInitializationException e) {
					LOGGER.error(e);
					try {
						server.stop();
					} catch (Exception e1) {
						LOGGER.error(e);
					}
				}
			}

			@Override
			public void afterStarting() {
				try {
					LOGGER.info("Application service running on port : "
							+ containerCfg.getServerConfig().getAppConnector().getPort());

					postInitialize(containerCfg);

					server.join();

				} catch (InterruptedException | ServiceInitializationException e) {
					LOGGER.error(e);
					try {
						server.stop();
					} catch (Exception e1) {
						LOGGER.error(e);
					}
				}
			}

		});

		server.start();

	}

	/**
	 * Parse the input yml file and map the values to the application configuration
	 * object
	 * 
	 * @param arguments
	 *            Name of the input yml file
	 * @return Application configuration object
	 * @throws ConfigurationException
	 *             if unable to read the configuration file and map it to the app
	 *             config object.
	 */
	@SuppressWarnings("unchecked")
	private T loadConfiguration(String... arguments) throws ConfigurationException {
		ObjectMapper yamlParser = null;
		T configuration = null;
		try {
			final File configFile = new File(arguments[0]);
			String configFileExt = FilenameUtils.getExtension(configFile.toString());
			// configuration yml validation
			if (arguments.length != 1 || !CONFIG_FILE_EXTENSION.equals(configFileExt)) {
				LOGGER.error("Usage : java [-jar AppService.jar <config>.yml]");
			}

			if (!configFile.exists()) {
				throw new FileNotFoundException("File " + configFile + " not found");
			}

			yamlParser = new ObjectMapper(new YAMLFactory());
			configuration = yamlParser.readValue(configFile,
					(Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]);

		} catch (IOException ioe) {
			throw new ConfigurationException(ioe);
		}
		return configuration;
	}

	/**
	 * Run through the spring configuration input classes and register it with the
	 * spring di container
	 * 
	 * @param containerCfg
	 *            - application config object
	 * @param containerCtx
	 *            - application context object
	 * @throws ServiceInitializationException
	 *             if the argument is empty or null
	 */
	private void registerServices(T containerCfg, ContainerContext containerCtx) throws ServiceInitializationException {
		if (getServiceFactoryList() != null) {
			for (Class<? extends AbstractFactory<? extends Configuration>> serviceFactory : getServiceFactoryList()) {
				containerCtx.registerServices(serviceFactory);
			}
		}
	}

	/**
	 * Run through the input list of servlet filters and register it with the
	 * servlet context for the given url pattern
	 * 
	 * @param containerCtx
	 *            - appplication context object
	 * @throws ServiceInitializationException
	 *             if the input filter classes are not defined in the spring DI
	 *             container
	 */
	private void registerWebResourceFilters(ContainerContext containerCtx) throws ServiceInitializationException {
		ResourceFilterList resourceFilterList = getWebResourceFilterList();

		if (resourceFilterList != null && resourceFilterList.size() > 0) {
			for (FilterType<? extends Filter> filterType : resourceFilterList) {
				containerCtx.registerWebResourceFilter(filterType);
			}
		}
	}

	/**
	 * Run through the input list of websocket resource class names and add it to
	 * the initialized websocket container of the servlet context
	 * 
	 * @param containerCfg
	 *            - application config object
	 * @param containerCtx
	 *            - application context object
	 * @throws ServiceInitializationException
	 *             if input websocket class is not registered in spring DI container
	 *             or unable to deploy the same in websocket container
	 */
	private void registerWebsocketResources(T containerCfg, ContainerContext containerCtx)
			throws ServiceInitializationException {
		if (containerCtx.getWebsocketContext() != null && getWebsocketResourceList() != null) {

			containerCtx.getWebsocketContext().setDefaultMaxSessionIdleTimeout(
					Integer.parseInt(containerCfg.getServerConfig().getWebsocketHandler().getTimeout()));

			for (Class<? extends WebsocketResource> websocketResource : getWebsocketResourceList()) {
				containerCtx.registerWebsocketHandler(websocketResource);
			}
		}
	}

	/**
	 * Run through the input list of jersey resource classes and add it to the
	 * jersey servlet container, Initialize swagger configuration for auto-scanning
	 * annotated REST controller classes and register the swagger UI assets
	 * 
	 * @param containerCfg
	 *            - application config object
	 * @param containerCtx
	 *            - application context object
	 * @throws ServiceInitializationException
	 *             if the method is called with empty or null resource class names
	 */
	private void registerResources(T containerCfg, ContainerContext containerCtx)
			throws ServiceInitializationException {

		String swaggerPackages = "";

		if (getRestResourceList() != null) {
			for (Class<? extends AbstractResource<? extends Configuration>> restBinder : getRestResourceList()) {
				swaggerPackages = swaggerPackages + restBinder.getPackage().getName() + ",";
				containerCtx.registerResources(restBinder);
			}
		}

		containerCtx.registerResources(ServerApi.class);
		swaggerPackages = swaggerPackages + ServerApi.class.getPackage().getName();

		if (containerCtx.getSwaggerAssets() != null) {
			LOGGER.info("Registering swagger assets...");

			registerSwaggerAssets(containerCfg, containerCtx);

			BeanConfig swaggerConfig = new BeanConfig();
			swaggerConfig.setTitle(containerCfg.getServiceConfig().getName());
			swaggerConfig.setVersion(containerCfg.getServiceConfig().getVersion());
			swaggerConfig.setBasePath(containerCfg.getServerConfig().getResourceHandler().getRestApiCtx() != null
					&& containerCfg.getServerConfig().getResourceHandler().getRestApiCtx().charAt(
							containerCfg.getServerConfig().getResourceHandler().getRestApiCtx().length() - 1) != '/'
									? containerCfg.getServerConfig().getResourceHandler().getRestApiCtx()
									: containerCfg.getServerConfig().getResourceHandler().getRestApiCtx().substring(0,
											containerCfg.getServerConfig().getResourceHandler().getRestApiCtx().length()
													- 1));

			LOGGER.debug("Swagger scanning api packages : " + swaggerPackages);
			swaggerConfig.setResourcePackage(swaggerPackages);
			swaggerConfig.setScan(true);

			containerCtx.registerResources(io.swagger.jaxrs.listing.ApiListingResource.class,
					io.swagger.jaxrs.listing.SwaggerSerializers.class);
		}
	}

	/**
	 * Create directory to extract the swagger assets from the application jar file
	 * 
	 * @return reference to the directory
	 */
	private static File createTempDir() {
		File baseDir = new File(System.getProperty("java.io.tmpdir"));
		String baseName = System.currentTimeMillis() + "-";

		for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
			File tempDir = new File(baseDir, baseName + counter);
			if (tempDir.mkdir()) {
				return tempDir;
			}
		}
		throw new IllegalStateException("Failed to create directory within " + TEMP_DIR_ATTEMPTS + " attempts (tried "
				+ baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
	}

	/**
	 * Deploy swagger UI assets into the configured server static resource handler
	 * path
	 * 
	 * @param containerCfg
	 *            - application configuration object
	 * @param containerCtx
	 *            - application context object
	 * @throws ServiceInitializationException
	 *             if unable to deploy the swagger UI assets
	 */
	private void registerSwaggerAssets(T containerCfg, ContainerContext containerCtx)
			throws ServiceInitializationException {
		JarFile jarFile = null;
		File tmpDirectory = null;
		try {

			URL swaggerUiPath = this.getClass().getResource(containerCtx.getSwaggerAssets());

			String jarPath = swaggerUiPath.toString().substring(0, swaggerUiPath.toString().lastIndexOf("!"));

			String OS = System.getProperty("os.name").toLowerCase();

			if (OS.indexOf("win") >= 0) {
				jarPath = jarPath.substring("jar:file:/".length());
			} else {
				jarPath = jarPath.substring("jar:file:".length());
			}

			jarFile = new JarFile(jarPath);

			final Enumeration<JarEntry> entries = jarFile.entries();

			String swaggerFolderName = containerCtx.getSwaggerAssets().replace("/", "");

			tmpDirectory = createTempDir();

			while (entries.hasMoreElements()) {
				JarEntry jarEntry = entries.nextElement();

				if (jarEntry.getName().startsWith(swaggerFolderName)) {
					File jarEntryFile = new File(tmpDirectory.getAbsolutePath() + File.separator + jarEntry.getName());
					if (jarEntry.isDirectory()) {
						jarEntryFile.mkdir();
						continue;
					}
					InputStream jarEntryFileInputStream = jarFile.getInputStream(jarEntry);

					FileOutputStream jarEntryFileOutputStream = new FileOutputStream(jarEntryFile);
					while (jarEntryFileInputStream.available() > 0) {
						jarEntryFileOutputStream.write(jarEntryFileInputStream.read());
					}
					jarEntryFileOutputStream.close();
					jarEntryFileInputStream.close();
				}
			}

			FileUtils.copyDirectoryToDirectory(
					new File(tmpDirectory.getAbsolutePath() + File.separator + swaggerFolderName),
					new File(containerCfg.getServerConfig().getResourceHandler().getResourceBase()));

		} catch (IOException e) {
			throw new ServiceInitializationException(e);
		} finally {
			try {
				if (jarFile != null)
					jarFile.close();
				if (tmpDirectory != null)
					tmpDirectory.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Method to be overridden by the application service class to setup or
	 * initialize the service environment with required configuration values before
	 * the service starts
	 * 
	 * @param containerCfg
	 *            - application configuration object
	 * @throws ServiceInitializationException
	 *             if unable to pre-initialize service environment with desired
	 *             configuration values
	 */
	protected abstract void preInitialize(T containerCfg) throws ServiceInitializationException;

	/**
	 * Method to be overridden by the application service class to setup the service
	 * environment with required configuration values during service start-up
	 * 
	 * @param containerCfg
	 *            - application configuration object
	 * @param containerCtx
	 *            - application context object
	 * @throws ServiceInitializationException
	 *             if unable to initialize service environment with desired
	 *             configuration values
	 */
	protected abstract void initialize(T containerCfg, ContainerContext containerCtx)
			throws ServiceInitializationException;

	/**
	 * Method to be overridden by the application service class to setup the service
	 * environment with required configuraiton values post service start up
	 * 
	 * @param containerCfg-
	 *            application config object
	 * @throws ServiceInitializationException
	 *             if unable to initialize service environment with desired
	 *             configuration values
	 */
	protected abstract void postInitialize(T containerCfg) throws ServiceInitializationException;

}