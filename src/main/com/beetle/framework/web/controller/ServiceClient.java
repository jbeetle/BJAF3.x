package com.beetle.framework.web.controller;

class ServiceClient extends com.beetle.framework.business.service.ServiceFactory {
	private static ServiceClient instance = new ServiceClient();

	private ServiceClient() {
	}

	public static ServiceClient getInstance() {
		return instance;
	}

	public <T> T rpcServiceLookupExt(final Class<T> interfaceClass, final String host, final int port,
			boolean withShortConnection) {
		return this.rpcServiceLookup(interfaceClass, host, port, withShortConnection);
	}

	public <T> T serviceLookupExt(final Class<T> interfaceClass) {
		return this.serviceLookup(interfaceClass);
	}
}
