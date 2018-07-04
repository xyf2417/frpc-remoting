package xyf.frpc.remoting.config;

import xyf.frpc.rpc.Invoker;

public class AbstractExporter<T> implements Exporter<T> {

	private Invoker<T> invoker;

	public Invoker<T> getInvoker() {
		return invoker;
	}

	public void setInvoker(Invoker<T> invoker) {
		this.invoker = invoker;
	}

	public void unexport() {
		throw new UnsupportedOperationException();
	}

}
