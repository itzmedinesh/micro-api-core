package com.itzmeds.mac.core.container;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.util.component.LifeCycle.Listener;

/**
 * Class implementing the application server life-cycle methods
 * 
 * @author itzmeds
 *
 */
public abstract class ContainerLifecycle implements Listener {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger(ContainerLifecycle.class);

	ContainerContext containerCtx;

	public ContainerLifecycle(ContainerContext containerCtx) {
		this.containerCtx = containerCtx;
	}

	@Override
	public void lifeCycleStarting(LifeCycle event) {
		whileStarting();
	}

	@Override
	public void lifeCycleStarted(LifeCycle event) {
		if (containerCtx.getAdapterContext() != null) {
			containerCtx.getAdapterContext().refresh();
		}
		afterStarting();
	}

	@Override
	public void lifeCycleStopping(LifeCycle event) {
	}

	@Override
	public void lifeCycleStopped(LifeCycle event) {
	}

	@Override
	public void lifeCycleFailure(LifeCycle event, Throwable cause) {
	}

	/**
	 * Method invoked when application server is starting
	 */
	public abstract void whileStarting();

	/**
	 * Method invoked after application server has started
	 */
	public abstract void afterStarting();
	
	/**
	 * Method invoked when application server is stopping
	 */
	public abstract void whileStopping();

}
