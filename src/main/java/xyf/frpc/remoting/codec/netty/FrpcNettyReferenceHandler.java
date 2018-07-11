package xyf.frpc.remoting.codec.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import xyf.frpc.remoting.handler.ResultHandler;
import xyf.frpc.rpc.data.Response;

public class FrpcNettyReferenceHandler extends ChannelInboundHandlerAdapter {
	private ResultHandler resultHandler;
	public FrpcNettyReferenceHandler(ResultHandler resultHandler) {
		this.resultHandler = resultHandler;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		Response response = (Response)msg;
		resultHandler.received(msg);
	}
}
