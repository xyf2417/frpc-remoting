package xyf.frpc.remoting.data;

import java.io.Serializable;

public class RequestBody implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2946565368837555734L;
	
	private String interfaceFullName;
	
	private String methodName;


	private Class[] parameterTypes;
	
	private Object[] arguments;
	
	public String getInterfaceFullName() {
		return interfaceFullName;
	}

	public void setInterfaceFullName(String interfaceFullName) {
		this.interfaceFullName = interfaceFullName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Class[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

}
