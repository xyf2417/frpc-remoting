package xyf.frpc.remoting.config;

import xyf.frpc.rpc.Invoker;

public class FrpcExporter<T> implements Exporter<T> {

	private Invoker<T> invoker;

	public Invoker<T> getInvoker() {
		return this.invoker;
	}

	public void setInvoker(Invoker<T> invoker) {
		this.invoker = invoker;
	}

	public void unexport() {

	}

}
