package xyf.frpc.remoting.server;

import xyf.frpc.remoting.handler.ResultHandler;
import xyf.frpc.rpc.RpcException;

public interface ProviderServer {

	public void bind(int port) throws RpcException;

	public void setResultHandler(ResultHandler resultHandler);

}
