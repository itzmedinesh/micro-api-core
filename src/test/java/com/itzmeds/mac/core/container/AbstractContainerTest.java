package com.itzmeds.mac.core.container;

import java.net.URI;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Test;

import com.itzmeds.mac.core.server.TestConfiguration;
import com.itzmeds.mac.core.service.FactoryList;
import com.itzmeds.mac.core.service.FilterType;
import com.itzmeds.mac.core.service.ResourceFilterList;
import com.itzmeds.mac.core.service.ResourceList;
import com.itzmeds.mac.core.service.WebsocketResourceList;
import com.itzmeds.mac.exception.ServiceCleanupException;
import com.itzmeds.mac.exception.ServiceInitializationException;

public class AbstractContainerTest extends AbstractContainer<TestConfiguration> {

	@Test
	public void testBoot() throws Exception {
		boot("test.yml");
	}

	@Override
	public ResourceList getRestResourceList() throws ServiceInitializationException {
		return new ResourceList(TestRestResource.class);
	}

	@Override
	public FactoryList getServiceFactoryList() throws ServiceInitializationException {
		return new FactoryList(TestFactoryImpl.class);
	}

	@Override
	public WebsocketResourceList getWebsocketResourceList() throws ServiceInitializationException {
		return new WebsocketResourceList(TestWebsocketResource.class);
	}

	@Override
	public ResourceFilterList getWebResourceFilterList() throws ServiceInitializationException {
		return new ResourceFilterList(new FilterType<TestFilter>(TestFilter.class, "/test/*"));
	}

	@Override
	protected void preInitialize(TestConfiguration containerCfg) throws ServiceInitializationException {
		Assert.assertTrue(containerCfg != null);
	}

	@Override
	protected void initialize(TestConfiguration containerCfg, ContainerContext containerCtx)
			throws ServiceInitializationException {
		Assert.assertTrue(containerCfg != null);
		Assert.assertTrue(containerCtx != null);
	}

	@Override
	protected void postInitialize(TestConfiguration containerCfg) throws ServiceInitializationException {

		Assert.assertTrue(containerCfg != null);

		try {

			Assert.assertTrue(testRestResource(containerCfg));

			Assert.assertTrue(testWebsocket(containerCfg));

			server.stop();
			server.join();
		} catch (Exception e) {
			Assert.fail();
		}

	}

	public boolean testWebsocket(TestConfiguration containerCfg) {
		try {

			WebSocketContainer container = ContainerProvider.getWebSocketContainer();

			ClientEndpointConfig clientEndpointConfig = ClientEndpointConfig.Builder.create()
					.configurator(new ClientEndpointConfig.Configurator() {
					}).build();

			Session clientSession = container.connectToServer(WSClient.class, clientEndpointConfig,
					URI.create("ws://localhost:" + containerCfg.getServerConfig().getAppConnector().getPort().toString()
							+ "/api/testlist"));

			Assert.assertTrue(clientSession != null);

			clientSession.getBasicRemote().sendText("hello websock server");

		} catch (Throwable e) {
			return false;
		}

		return true;
	}

	public boolean testRestResource(TestConfiguration containerCfg) {

		String url = "http://localhost:" + containerCfg.getServerConfig().getAppConnector().getPort() + "/"
				+ containerCfg.getServerConfig().getResourceHandler().getRestApiCtx() + "test/hello";

		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(url);

		HttpResponse response;
		try {
			response = client.execute(request);

			Assert.assertEquals(200, response.getStatusLine().getStatusCode());

		} catch (Throwable e) {
			return false;

		}
		return true;
	}

	@Override
	protected void preDestroy(TestConfiguration containerCfg) throws ServiceCleanupException {
		Assert.assertTrue(containerCfg != null);		
	}

}
