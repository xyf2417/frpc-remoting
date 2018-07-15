package xyf.frpc.remoting.handler;

import io.netty.channel.Channel;

public interface ResultHandler {
	
	public Object received(Object msg, Channel nettyChannel);
	
	public void setAttachment(Object attachment);

	public Object getAttachment();
	
	public boolean isProvider();
	
	public void setIsProvider(boolean isProvider);
	

}
