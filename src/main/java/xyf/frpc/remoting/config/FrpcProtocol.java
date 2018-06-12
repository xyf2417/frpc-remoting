package xyf.frpc.remoting.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import xyf.frpc.config.ProtocolConfig;
import xyf.frpc.config.Provider;
import xyf.frpc.config.util.ExtensionLoader;
import xyf.frpc.remoting.server.ProviderServer;
import xyf.frpc.rpc.AbstractInvoker;
import xyf.frpc.rpc.DefaultInvoker;
import xyf.frpc.rpc.Invoker;
import xyf.frpc.rpc.proxy.ProxyFactory;

public class FrpcProtocol implements Protocol {
	
	private ProxyFactory proxyFactory = (ProxyFactory) ExtensionLoader.getExtensionLoader(ProxyFactory.class).getExtension("jdk");
	
	private Map<String, Invoker<?>> invokerMap = new ConcurrentHashMap<String, Invoker<?>>();
	
	private Map<String, ProviderServer> serverMap = new ConcurrentHashMap<String, ProviderServer>();

	public <T> Exporter<T> export(String providerName, ProtocolConfig protocolConfig, Provider provider)
	{
		Object proxy = proxyFactory.getProxy(provider.getInterface(), provider.getTarget());
		AbstractInvoker<?> invoker = new DefaultInvoker();
		invoker.setProxy(proxy);
		invoker.setInterface(provider.getInterface());
		invokerMap.putIfAbsent(provider.getInterface().getName(), invoker);
		
		Exporter exporter = new FrpcExporter();
		exporter.setInvoker(invoker);
		
		String serverKey = protocolConfig.getServerKey();
		
		ProviderServer providerServer = serverMap.get(serverKey);
		if(providerServer == null) {
			providerServer = createServer(protocolConfig.getPort());
			serverMap.put(serverKey, providerServer);
		}
		
		return exporter;
	}
	
	private ProviderServer createServer(int port) {
		ProviderServer providerServer = (ProviderServer) ExtensionLoader.getExtensionLoader(ProviderServer.class).getExtension("netty");
	    providerServer.bind(port);
		return providerServer;
	}
	

}
