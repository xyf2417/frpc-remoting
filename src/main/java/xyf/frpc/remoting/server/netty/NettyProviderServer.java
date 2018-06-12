package xyf.frpc.remoting.server.netty;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import xyf.frpc.remoting.codec.netty.NettyRequestCoder;
import xyf.frpc.remoting.server.ProviderServer;

public class NettyProviderServer implements ProviderServer {
	
	private static final Log logger = LogFactory.getLog(NettyProviderServer.class);
	
	public void bind(int port) {
		
		logger.info("frpc:" + " start server binding");
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1024)
			.childHandler(new ChildChannelHandler());
			ChannelFuture f = b.bind(port);
			
			logger.info("frpc:" + " server is listerning at " + port);
			
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}

	}

}

class ChildChannelHandler extends ChannelInitializer<Channel> {

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ch.pipeline().addLast(new TimerServerHandler());
	}
}

class TimerServerHandler extends ChannelInboundHandlerAdapter  {
	NettyRequestCoder coder= new NettyRequestCoder();
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("server channelRead");
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
