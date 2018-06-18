package com.itzmeds.mac.core.container;

import com.itzmeds.mac.core.service.FactoryList;
import com.itzmeds.mac.core.service.ResourceFilterList;
import com.itzmeds.mac.core.service.ResourceList;
import com.itzmeds.mac.core.service.WebsocketResourceList;
import com.itzmeds.mac.exception.ServiceInitializationException;

/**
 * Interface provides set of methods to be implemented by the application
 * service class
 * 
 * @author itzmeds
 *
 */
public interface Container {

	/**
	 * Method to be implemented by the application service class to register jersey
	 * resource classes with the jersey servlet container
	 * 
	 * @return list of jersey rest resource controller class names
	 * @throws ServiceInitializationException
	 */
	public ResourceList getRestResourceList() throws ServiceInitializationException;

	/**
	 * Method to be implemented by the application service class to register spring
	 * configuration classes with the spring DI container
	 * 
	 * @return list of spring configuration class names
	 * @throws ServiceInitializationException
	 * 
	 */
	public FactoryList getServiceFactoryList() throws ServiceInitializationException;

	/**
	 * Method to be implemented by the application service class to register
	 * web-socket resource class names with the initialized web-socket container of
	 * the servlet context
	 * 
	 * @return list of web-socket handler class names
	 * @throws ServiceInitializationException
	 */
	public WebsocketResourceList getWebsocketResourceList() throws ServiceInitializationException;

	/**
	 * Method to be implemented by the application service class to register servlet
	 * filters with the servlet context for the given url pattern
	 * 
	 * @return list of servlet filter class names
	 * @throws ServiceInitializationException
	 */
	public ResourceFilterList getWebResourceFilterList() throws ServiceInitializationException;

}