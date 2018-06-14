package xyf.frpc.remoting.server;

import xyf.frpc.remoting.handler.ResultHandler;

public interface ProviderServer {
	
	public void bind(int port);
	
	public void setResultHandler(ResultHandler resultHandler);
	
}
