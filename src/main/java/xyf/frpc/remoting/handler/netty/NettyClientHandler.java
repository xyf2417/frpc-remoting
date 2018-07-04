package xyf.frpc.remoting.handler.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import xyf.frpc.remoting.codec.netty.NettyResponseCoder;
import xyf.frpc.remoting.handler.ResultHandler;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {

	private ResultHandler resultHandler;

	private NettyResponseCoder coder;

	public NettyClientHandler(ResultHandler resultHandler) {
		this.resultHandler = resultHandler;
		this.coder = new NettyResponseCoder(resultHandler);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		coder.decode(msg, ctx);
		ctx.read();
	}
}
