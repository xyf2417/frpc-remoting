package xyf.frpc.remoting.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xyf.frpc.remoting.handler.ResultHandler;
import xyf.frpc.remoting.handler.netty.NettyServerHandler;
import xyf.frpc.remoting.server.ProviderServer;

public class NettyProviderServer implements ProviderServer {

	private static final Log logger = LogFactory
			.getLog(NettyProviderServer.class);

	private ResultHandler resultHandler;

	private ChannelFuture nettyChannelFuture;

	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	public void bind(int port) {

		logger.info("frpc:" + " start server binding");
		bossGroup = new NioEventLoopGroup();
		workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 1024)
					.childHandler(new ChildChannelHandler(resultHandler));

			nettyChannelFuture = b.bind(port).sync();

			logger.info("frpc:" + " server is listerning at " + port);

		} catch (InterruptedException e) {
			logger.info("frpc:" + " server interrupted, the nested reason is "
					+ e.getMessage());
		} finally {
			// no op
		}

	}

	public void setResultHandler(ResultHandler resultHandler) {
		this.resultHandler = resultHandler;
	}

}

class ChildChannelHandler extends ChannelInitializer<Channel> {

	private ResultHandler resultHandler;

	public ChildChannelHandler(ResultHandler resultHandler) {
		this.resultHandler = resultHandler;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ch.pipeline().addLast(new NettyServerHandler(resultHandler));
	}
}
