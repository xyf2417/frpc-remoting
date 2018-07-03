package xyf.frpc.remoting.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import xyf.frpc.remoting.RpcException;
import xyf.frpc.remoting.client.ReferenceClient;
import xyf.frpc.remoting.handler.ResultHandler;
import xyf.frpc.remoting.handler.netty.NettyClientHandler;
import xyf.frpc.rpc.Invocation;
import xyf.frpc.rpc.ResponseFuture;

public class NettyReferenceClient implements ReferenceClient {

	private ResultHandler resultHandler;
	
	private ChannelFuture nettyChannelFuture;
	
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	
	public void connect(String ip, int port) throws RpcException{
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(new NettyClientHandler(resultHandler));
				}
			});
			nettyChannelFuture = b.connect(ip, port).sync();
		} catch (InterruptedException e) {
			throw new RpcException(e.getMessage());
		} finally {
			//no op;
		}

	}

	public void setResultHandler(ResultHandler resultHandler) {
		this.resultHandler = resultHandler;		
	}

	public ResponseFuture request(Invocation invocation) throws RpcException{

		return null;
	}

}