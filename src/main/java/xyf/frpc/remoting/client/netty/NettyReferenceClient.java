package xyf.frpc.remoting.client.netty;

import xyf.frpc.remoting.client.ReferenceClient;
import xyf.frpc.remoting.handler.ResultHandler;

public class NettyReferenceClient implements ReferenceClient {

	private ResultHandler resultHandler;
	
	public void connect(String ip, int port) {
		

	}

	public void setResultHandler(ResultHandler resultHandler) {
		this.resultHandler = resultHandler;		
	}

}
