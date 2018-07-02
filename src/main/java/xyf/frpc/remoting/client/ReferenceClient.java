package xyf.frpc.remoting.client;

import xyf.frpc.remoting.handler.ResultHandler;

public interface ReferenceClient {
	
	public void connect(String ip, int port);
	
	public void setResultHandler(ResultHandler resultHandler);

}
