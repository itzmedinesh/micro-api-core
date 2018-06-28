package com.itzmeds.mac.core.server;

import java.lang.management.ManagementFactory;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.itzmeds.mac.core.service.AbstractResource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * REST API to manage application server
 * 
 * @author itzmeds
 *
 */
@Api(value = "Server Management API")
@Path("/server")
public class ServerApi extends AbstractResource {

	private static final Logger LOGGER = LogManager.getLogger(ServerApi.class);

	@ApiOperation(value = "Get server process id")
	@GET
	@Path("/getpid")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProcessId() {
		String status = "UNKNOWN";
		String response = null;

		try {
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			ObjectName mbeanName = new ObjectName("java.lang:type=Runtime");

			ObjectName objectInstance = mBeanServer.getObjectInstance(mbeanName).getObjectName();

			status = "" + mBeanServer.getAttribute(objectInstance, "Name");

			if (status != null)
				status = status.split("@")[0];

			response = "{\"processId\":\"" + status + "\"}";

			// LOGGER.info(response);

		} catch (MalformedObjectNameException | InstanceNotFoundException | AttributeNotFoundException
				| ReflectionException | MBeanException e) {
			LOGGER.error(e.getMessage());
			response = "{\"error\":\" could not find server pid\"}";
		}
		return Response.ok(response).build();
	}

}
