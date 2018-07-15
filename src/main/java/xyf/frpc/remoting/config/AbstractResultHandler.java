package xyf.frpc.remoting.config;

import xyf.frpc.remoting.handler.ResultHandler;

public abstract class AbstractResultHandler implements ResultHandler {

	private Object attachment;
	
	private boolean isProvider;
	
	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	public Object getAttachment() {
		return this.attachment;
	}
	
	public boolean isProvider() {
		return this.isProvider;
	}
	
	public void setIsProvider(boolean isProvider) {
		this.isProvider = isProvider;
	}
	

}
