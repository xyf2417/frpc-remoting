package xyf.frpc.remoting.config;

import xyf.frpc.rpc.Invoker;

public interface Protocol {
	public <T> Exporter<T> export(ExportInfo exportInfo, Invoker<?> invoker);
}
