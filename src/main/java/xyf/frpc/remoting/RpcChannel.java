package xyf.frpc.remoting;

import io.netty.channel.Channel;

public interface RpcChannel {
	/**
	 * Set the attribute with the specified key to this channel
	 * 
	 * @param key the attribute's key
	 * @param value new value of this attribute
	 * @return the old value if exists
	 */
	Object addAttribute(String key, Object value);
	
	Object getAttribute(String key);
	
	Channel getNettyChannel();
	
	void setNettyChannel(Channel channel);
}
