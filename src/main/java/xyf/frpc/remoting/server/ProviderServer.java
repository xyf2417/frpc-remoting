package xyf.frpc.remoting.server;

import xyf.frpc.remoting.RpcException;
import xyf.frpc.remoting.handler.ResultHandler;

public interface ProviderServer {

	public void bind(int port) throws RpcException;

	public void setResultHandler(ResultHandler resultHandler);

}
