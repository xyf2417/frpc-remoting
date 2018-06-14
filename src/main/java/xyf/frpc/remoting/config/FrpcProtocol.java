package xyf.frpc.remoting.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xyf.frpc.config.util.ExtensionLoader;
import xyf.frpc.remoting.data.Head;
import xyf.frpc.remoting.data.Request;
import xyf.frpc.remoting.data.Response;
import xyf.frpc.remoting.data.ResponseBody;
import xyf.frpc.remoting.handler.ResultHandler;
import xyf.frpc.remoting.server.ProviderServer;
import xyf.frpc.rpc.Invocation;
import xyf.frpc.rpc.Invoker;
import xyf.frpc.rpc.MethodInvocation;
import xyf.frpc.rpc.Result;
import xyf.frpc.rpc.ResultStatus;
import xyf.frpc.rpc.RpcResult;

public class FrpcProtocol implements Protocol {
	
	private final static Log logger = LogFactory.getLog(FrpcProtocol.class);
	
	private Map<String, Invoker<?>> invokerMap = new ConcurrentHashMap<String, Invoker<?>>();
	
	private Map<String, ProviderServer> serverMap = new ConcurrentHashMap<String, ProviderServer>();
	
	private ResultHandler resultHandler = new ResultHandler() {

		public Object received(Object msg) {
			
			Request request = (Request)msg;
			Invoker<?> invoker = invokerMap.get(request.getBody().getInterfaceFullName());
			Head head = new Head();
			head.setMagic(Head.MAGIC);
			head.setInvokeId(request.getHead().getInvokeId());
			
			ResponseBody body = new ResponseBody();
			
			if(invoker == null) {
				Result result = new RpcResult();
				result.setStatus(ResultStatus.ERROR);
				result.setValue("Can't find the provider for interface " + request.getBody().getInterfaceFullName());
				body.setReturnValue(result);
				
				if(logger.isErrorEnabled()) {
					logger.error("frpc: Can't find the provider for interface " + request.getBody().getInterfaceFullName());
				}
			} 
			else {
				
				Invocation invocation = new MethodInvocation();
				invocation.setInterfaceFullName(request.getBody().getInterfaceFullName());
				invocation.setMethodName(request.getBody().getMethodName());
				invocation.setArguments(request.getBody().getArguments());
				invocation.setParameterTypes(request.getBody().getParameterTypes());
				
				Result result = invoker.invoke(invocation);
				
				body.setReturnValue(result);
			}
			
			Response response = new Response();
			response.setHead(head);
			response.setBody(body);
			return response;
		}
		
	};

	public <T> Exporter<T> export(ExportInfo exportInfo, Invoker<?> invoker)
	{
		invokerMap.put(invoker.getInterface().getName(), invoker);
		
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
		providerServer.setResultHandler(resultHandler);
	    providerServer.bind(port);
		return providerServer;
	}

}
