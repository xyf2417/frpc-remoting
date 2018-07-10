package xyf.frpc.remoting.config;

import xyf.frpc.remoting.RpcException;
import xyf.frpc.rpc.Invoker;

public interface Protocol {
	public <T> Exporter<T> export(ExportInfo exportInfo, Invoker<?> invoker) throws RpcException;

	public <T> Invoker<T> refer(BindInfo bindInfo, Invoker<?> invoker)
			throws RpcException;
}
