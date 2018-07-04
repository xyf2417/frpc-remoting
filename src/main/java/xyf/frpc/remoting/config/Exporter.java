package xyf.frpc.remoting.config;

import xyf.frpc.rpc.Invoker;

public interface Exporter<T> {

	Invoker<T> getInvoker();

	void setInvoker(Invoker<T> invoker);

	void unexport();
}
