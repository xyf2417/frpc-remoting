package xyf.frpc.remoting.client;

import xyf.frpc.remoting.RpcException;
import xyf.frpc.remoting.handler.ResultHandler;
import xyf.frpc.rpc.Invocation;
import xyf.frpc.rpc.ResponseFuture;

public interface ReferenceClient {
	
	public void connect(String ip, int port) throws RpcException;
	
	public void setResultHandler(ResultHandler resultHandler);
	
	public ResponseFuture request(Invocation invocation) throws RpcException;

}
