package xyf.frpc.remoting.config;

import java.net.InetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BindInfo {

	private final static Log logger = LogFactory.getLog(BindInfo.class);

	private String ip;

	private int port;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public static String getLocalHostIp() {
		InetAddress ia = null;
		try {
			ia = InetAddress.getLocalHost();
			String localip = ia.getHostAddress();
			return localip;
		} catch (Exception e) {
			logger.warn("frpc: can't not get local host ip");
			return null;
		}
	}

	public static String getLocalHostName() {
		InetAddress ia = null;
		try {
			ia = InetAddress.getLocalHost();
			String localname = ia.getHostName();
			return localname;
		} catch (Exception e) {
			logger.warn("frpc: can't not get local host name");
			return null;
		}
	}

	public static BindInfo buildBindInfo(String ip, int port) {
		BindInfo bi = new BindInfo();
		bi.ip = ip;
		bi.port = port;
		return bi;
	}

	public String getClientKey() {
		return this.ip + ":" + port;
	}
}
