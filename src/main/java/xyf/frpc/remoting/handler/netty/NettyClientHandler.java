package xyf.frpc.remoting.handler.netty;

import xyf.frpc.remoting.codec.netty.NettyResponseCoder;
import xyf.frpc.remoting.handler.ResultHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

	private ResultHandler resultHandler;
	
	private NettyResponseCoder coder;
	
	public NettyClientHandler(ResultHandler resultHandler) {
		this.resultHandler = resultHandler;
		this.coder = new NettyResponseCoder(resultHandler);
	}
}
