# Micro API

Java framework for rapid development of enterprise class RESTful web services using best in class java libraries. Key features of micro api framework:

__**Versions on maven**__

http://central.maven.org/maven2/com/github/itzmedinesh/micro-api-core/

__**Live above abstraction**__

There aren’t too many abstraction code and annotations to hide internals of the framework, and hence the code developers and maintenance engineers have complete control of the application/framework code and its  behaviour.  The framework enables easy and quick launch of micro services, add new functionality, extend, modify, test, troubleshoot and debug.

__**Portable Business Object**__

Build portable business objects that are not tied to specific framework implementation. With micro api, the business objects remain plain old java objects (POJOs) and hence its easy to port them across other frameworks.


__**Tried & Tested Standard Libraries**__

Micro api encompasses best in class standard, tried and tested java libraries such as Jersey, Jetty,  Spring DI, Spring Integration, Jackson, Web-sockets, Swagger, YAML etc. The framework provides a clear and straight forward interfaces for rapid development of enterprise class APIs. 


__**REST & Web-Socket APIs**__

Micro Api framework provides features to quickly develop REST and web-socket based synchronous, asynchronous and reactive APIs. 


__**Light-Weight Integration Engine**__

Spawn a light weight integration engine to listen and react to the events from the enterprise systems. With spring integration libraries bundled into the framework, adding resource adapters to consume or send events from and to enterprise apps is easy and quick.


__**Cloud Ready**__

Inspired by 12 factor app rules, micro api provides the right framework for the applications to be built and deployed on cloud environment.


__**Quick Start Micro Api**__

```

public class AppService extends AbstractContainer<AppConfiguration> {

	public static void main(String[] args) throws Exception {
		new AppService().boot(args);
	}
	
	@Override
	protected void preInitialize(AppConfiguration processContainerCfg) throws ServiceInitializationException {
	}

	@Override
	public void initialize(AppConfiguration configuration, ContainerContext containerCtx)
			throws com.itzmeds.mac.exception.ServiceInitializationException {
		containerCtx.setSwaggerAssets("/swaggerui");
	}
	
	@Override
	protected void postInitialize(AppConfiguration processContainerCfg) throws ServiceInitializationException {
	}

	@Override
	public ResourceList getRestResourceList() throws ServiceInitializationException {
		return new ResourceList(TestApi.class);
	}

	@Override
	public FactoryList getServiceFactoryList() throws ServiceInitializationException {
		return new FactoryList(AppFactoryImpl.class);
	}

	@Override
	public WebsocketResourceList getWebsocketResourceList() throws ServiceInitializationException {
		return new WebsocketResourceList(ProductListResource.class, PriceListResource.class);
	}

	@Override
	public ResourceFilterList getWebResourceFilterList() throws ServiceInitializationException {
		return new ResourceFilterList(new FilterType<TestFilter2>(TestFilter2.class, "/test/*"),
				new FilterType<TestFilter>(TestFilter.class, "/test/*"));
	}
}

```
