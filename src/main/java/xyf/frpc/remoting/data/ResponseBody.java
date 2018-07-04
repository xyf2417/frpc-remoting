package xyf.frpc.remoting.data;

import java.io.Serializable;

public class ResponseBody implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5069245610440340300L;

	private Object returnValue;

	public Object getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(Object returnValue) {
		this.returnValue = returnValue;
	}

	public String toString() {
		return returnValue.toString();
	}

}
