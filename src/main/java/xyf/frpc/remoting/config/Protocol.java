package xyf.frpc.remoting.config;

import xyf.frpc.config.ProtocolConfig;
import xyf.frpc.config.Provider;

public interface Protocol {
	public <T> Exporter<T> export(String providerName, ProtocolConfig protocolConfig, Provider provider);
}
