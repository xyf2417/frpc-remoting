package xyf.frpc.remoting.codec.netty;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import xyf.frpc.remoting.handler.ResultHandler;
import xyf.frpc.rpc.data.Response;

public class FrpcNettyReferenceHandler extends ChannelInboundHandlerAdapter {
	private static final Log logger = LogFactory
			.getLog(FrpcNettyReferenceHandler.class);
	private ResultHandler resultHandler;
	public FrpcNettyReferenceHandler(ResultHandler resultHandler) {
		this.resultHandler = resultHandler;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		Response response = (Response)msg;
		resultHandler.received(response, ctx.channel());
	}
	
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
		Channel channel = ctx.channel();
		String key = getChannelKey(channel);
		if(logger.isWarnEnabled()) {
			logger.warn("frpc: lost connection with service  " + key + ", the nested reason is " + cause.getMessage());
		}
    }
	
	private String getChannelKey(Channel nettyChannel) {
		String key = nettyChannel.remoteAddress().toString();
		key.replace("/", "");
		return key;
	}
}
