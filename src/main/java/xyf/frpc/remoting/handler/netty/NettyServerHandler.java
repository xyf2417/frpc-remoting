package xyf.frpc.remoting.handler.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import xyf.frpc.remoting.codec.netty.NettyRequestCoder;
import xyf.frpc.remoting.handler.ResultHandler;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

	private ResultHandler resultHandler;

	private NettyRequestCoder coder;
	
	public NettyServerHandler(ResultHandler resultHandler) {
		this.resultHandler = resultHandler;
		this.coder = new NettyRequestCoder(resultHandler);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		coder.decode(msg, ctx);
		ctx.read();
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
	}	
}
