package xyf.frpc.remoting.netty;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

import xyf.frpc.remoting.Constants;
import xyf.frpc.remoting.RpcChannel;

public class NettyRpcChannel implements RpcChannel {

	private final Map<String, Object> attributes = new HashMap<String, Object>();
	
	//instance initialize
	{
		attributes.put(Constants.FIRST_HEART_BEAT_KEY, "true");
		attributes.put(Constants.HEART_BEAT_LAST_RECV_TIME_KEY, System.currentTimeMillis());
	}
	public Channel nettyChannel;
	
	public NettyRpcChannel(){
	}
	
	public NettyRpcChannel(Channel nettyChannel) {
		this.nettyChannel = nettyChannel;
	}
	
	public synchronized Object addAttribute(String key, Object value) {
		return attributes.put(key, value);
	}

	public synchronized Object getAttribute(String key) {
		return attributes.get(key);
	}

	public Channel getNettyChannel() {
		return this.nettyChannel;
	}

	public void setNettyChannel(Channel channel) {
		this.nettyChannel = channel;
	}

}
