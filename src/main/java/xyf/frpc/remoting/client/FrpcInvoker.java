package xyf.frpc.remoting.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xyf.frpc.rpc.Invocation;
import xyf.frpc.rpc.Invoker;
import xyf.frpc.rpc.ResponseFuture;
import xyf.frpc.rpc.Result;
import xyf.frpc.rpc.ResultStatus;
import xyf.frpc.rpc.RpcException;
import xyf.frpc.rpc.RpcResult;

public class FrpcInvoker<T> implements Invoker<T> {

	private final static Log logger = LogFactory.getLog(FrpcInvoker.class);

	private ReferenceClient referenceClient;

	private Class interfaceClass;

	public FrpcInvoker(ReferenceClient rc, Class<T> cInterface) {
		this.referenceClient = rc;
		this.interfaceClass = cInterface;
	}

	public Class<T> getInterface() {
		return interfaceClass;
	}

	public void setInterface(Class<T> cInterface) {
		this.interfaceClass = cInterface;
	}

	public Result invoke(Invocation invocation)  throws RpcException {
		ResponseFuture future;
		Result result = new RpcResult();
		future = referenceClient.request(invocation);
		Object resultValue = future.get();
		result.setStatus(ResultStatus.NORMAL);
		result.setValue(resultValue);
		
		return result;
	}

}
