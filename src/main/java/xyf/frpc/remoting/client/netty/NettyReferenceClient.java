package xyf.frpc.remoting.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xyf.frpc.remoting.RpcException;
import xyf.frpc.remoting.client.ReferenceClient;
import xyf.frpc.remoting.codec.netty.FrpcNettyReferenceDecoder;
import xyf.frpc.remoting.codec.netty.FrpcNettyReferenceEncoder;
import xyf.frpc.remoting.codec.netty.FrpcNettyReferenceHandler;
import xyf.frpc.remoting.handler.ResultHandler;
import xyf.frpc.rpc.Invocation;
import xyf.frpc.rpc.ResponseFuture;
import xyf.frpc.rpc.data.Head;
import xyf.frpc.rpc.data.Request;
import xyf.frpc.rpc.data.RequestBody;

public class NettyReferenceClient implements ReferenceClient {
	/**
	 * scheduled for heartbeat
	 */
	ExecutorService scheduled = Executors.newScheduledThreadPool(1);

	private final static Log logger = LogFactory
			.getLog(NettyReferenceClient.class);

	private ResultHandler resultHandler;

	private ChannelFuture nettyChannelFuture;

	private Channel nettyChannel;

	private EventLoopGroup workerGroup;

	public void connect(String ip, int port) throws RpcException {
		workerGroup = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup).channel(NioSocketChannel.class)
					.option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<Channel>() {
						@Override
						protected void initChannel(Channel ch) throws Exception {
							ch.pipeline().addLast(
									new FrpcNettyReferenceDecoder());
							ch.pipeline().addLast(
									new FrpcNettyReferenceEncoder());
							ch.pipeline()
									.addLast(
											new FrpcNettyReferenceHandler(
													resultHandler));
						}
					});
			nettyChannelFuture = b.connect(ip, port).sync();
			nettyChannel = nettyChannelFuture.channel();

		} catch (Exception e) {
			throw new RpcException(e.getMessage());
		} finally {
			// no op;
		}

	}

	public void setResultHandler(ResultHandler resultHandler) {
		this.resultHandler = resultHandler;
	}

	public ResponseFuture request(Invocation invocation) throws RpcException {

		long invokeId = RequestBody.nextInvokeId();

		invocation.setInvokeId(invokeId);

		ResponseFuture future = new ResponseFuture(invocation);

		Head head = new Head();
		head.setMagic(Head.MAGIC_NUMBER);

		RequestBody body = new RequestBody();
		body.setInvokeId(invokeId);
		body.setArguments(invocation.getArguments());
		body.setInterfaceFullName(invocation.getInterfaceFullName());
		body.setMethodName(invocation.getMethodName());
		body.setParameterTypes(invocation.getParameterTypes());
		body.setEventType(RequestBody.EventType.RPC);

		Request request = new Request();
		request.setHead(head);
		request.setBody(body);

		nettyChannel.writeAndFlush(request);

		return future;
	}

}
