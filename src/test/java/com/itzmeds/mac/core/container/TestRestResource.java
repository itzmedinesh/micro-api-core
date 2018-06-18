package com.itzmeds.mac.core.container;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.itzmeds.mac.core.server.TestConfiguration;
import com.itzmeds.mac.core.server.TestSpringAdapterBean;
import com.itzmeds.mac.core.service.AbstractResource;

@Path("/test")
public class TestRestResource extends AbstractResource<TestConfiguration> {

	private static final Logger LOGGER = LogManager.getLogger(TestRestResource.class);

	@Inject
	TestSpringAdapterBean testSpringBean;

	@GET
	@Path("/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMsg(@PathParam("param") String msg) {

		String output = "{\"message\":\"" + msg + " " + testSpringBean.testSpringBeanName + "\"}";

		LOGGER.info(output);

		return Response.ok(output).build();
	}

}