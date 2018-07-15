package xyf.frpc.remoting.codec.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xyf.frpc.remoting.RpcChannel;
import xyf.frpc.remoting.handler.ResultHandler;
import xyf.frpc.remoting.netty.NettyRpcChannel;
import xyf.frpc.remoting.server.netty.NettyProviderServer;
import xyf.frpc.rpc.data.Response;

public class FrpcNettyServiceHandler extends ChannelInboundHandlerAdapter {
	private static final Log logger = LogFactory
			.getLog(FrpcNettyServiceHandler.class);
	
	private ResultHandler resultHandler;
	private NettyProviderServer providerServer;
	
	public FrpcNettyServiceHandler(ResultHandler resultHandler) {
		this.resultHandler = resultHandler;
		this.providerServer = (NettyProviderServer) resultHandler.getAttachment();
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		Response response = (Response) resultHandler.received(msg, ctx.channel());
		ctx.writeAndFlush(response);
	}
	
	@Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		Channel nettyChannel = ctx.channel();
		String key = getChannelKey(nettyChannel);
		RpcChannel channel = new NettyRpcChannel(nettyChannel);
		providerServer.addChannel(key, channel);
        //ctx.fireChannelRegistered();
    }
	
	 @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
		Channel channel = ctx.channel();
		String key = getChannelKey(channel);
		providerServer.removeChannel(key);
		if(logger.isWarnEnabled()) {
			logger.warn("frpc: reference " + key + " removed caused by " + cause.getMessage());
		}
    }
	 
	private String getChannelKey(Channel nettyChannel) {
		String key = nettyChannel.remoteAddress().toString();
		key = key.replace("/", "");
		return key;
	}
}
