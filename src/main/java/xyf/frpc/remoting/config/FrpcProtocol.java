package xyf.frpc.remoting.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xyf.frpc.config.util.ExtensionLoader;
import xyf.frpc.remoting.server.ProviderServer;
import xyf.frpc.rpc.Invoker;

public class FrpcProtocol implements Protocol {
	
	private final static Log logger = LogFactory.getLog(FrpcProtocol.class);
	
	private Map<String, Invoker<?>> invokerMap = new ConcurrentHashMap<String, Invoker<?>>();
	
	private Map<String, ProviderServer> serverMap = new ConcurrentHashMap<String, ProviderServer>();

	public <T> Exporter<T> export(ExportInfo exportInfo, Invoker<?> invoker)
	{
		
		
		Exporter exporter = new FrpcExporter();
		exporter.setInvoker(invoker);
		
		String serverKey = exportInfo.getServerKey();
		
		ProviderServer providerServer = serverMap.get(serverKey);
		if(providerServer == null) {
			providerServer = createServer(exportInfo.getPort());
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
