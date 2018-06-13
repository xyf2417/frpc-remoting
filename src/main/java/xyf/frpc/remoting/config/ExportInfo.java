package xyf.frpc.remoting.config;

import java.net.InetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExportInfo {
	
	private final static Log logger = LogFactory.getLog(ExportInfo.class);
	
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
		 InetAddress ia=null;
        try {
            ia=InetAddress.getLocalHost();
            String localip=ia.getHostAddress();
            return localip;
        } catch (Exception e) {
        	logger.warn("frpc: can't not get local host ip");
            return null;
        }
	}
	
	public static String getLocalHostName() {
		 InetAddress ia=null;
        try {
            ia=InetAddress.getLocalHost();   
            String localname=ia.getHostName();
            return localname;
        } catch (Exception e) {
        	logger.warn("frpc: can't not get local host name");
            return null;
        }
	}
	
	public static ExportInfo getLocalExportInfo(int port) {
		ExportInfo ei = new ExportInfo();
		ei.ip = getLocalHostIp();
		ei.port = port;
		return ei;
	}
	
	public String getServerKey() {
		return this.ip + ":" + port;
	}
}
