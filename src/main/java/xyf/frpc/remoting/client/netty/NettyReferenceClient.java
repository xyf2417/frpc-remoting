package xyf.frpc.remoting.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xyf.frpc.remoting.RpcException;
import xyf.frpc.remoting.client.ReferenceClient;
import xyf.frpc.remoting.codec.netty.JavaSerializableReqRespBodyPack;
import xyf.frpc.remoting.handler.ResultHandler;
import xyf.frpc.remoting.handler.netty.NettyClientHandler;
import xyf.frpc.rpc.Invocation;
import xyf.frpc.rpc.ResponseFuture;
import xyf.frpc.rpc.data.Head;
import xyf.frpc.rpc.data.RequestBody;

public class NettyReferenceClient implements ReferenceClient {
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
									new NettyClientHandler(resultHandler));
						}
					});
			nettyChannelFuture = b.connect(ip, port);
			nettyChannel = nettyChannelFuture.channel();

		} catch(Exception e) {
			throw new RpcException(e.getMessage());
		} finally {
			// no op;
		}

	}

	public void setResultHandler(ResultHandler resultHandler) {
		this.resultHandler = resultHandler;
	}

	public ResponseFuture request(Invocation invocation) throws RpcException {

		long invokeId = generateInvokeId();

		invocation.setInvokeId(invokeId);

		ResponseFuture future = new ResponseFuture(invocation);

		Head head = new Head();
		head.setMagic(Head.MAGIC);
		head.setInvokeId(invokeId);

		RequestBody body = new RequestBody();
		body.setArguments(invocation.getArguments());
		body.setInterfaceFullName(invocation.getInterfaceFullName());
		body.setMethodName(invocation.getMethodName());
		body.setParameterTypes(invocation.getParameterTypes());

		byte[] bodyBytes = null;
		try {
			bodyBytes = JavaSerializableReqRespBodyPack.toArray(body);
		} catch (IOException e) {
			logger.error("frpc: serializable request error");
		}

		head.setBodyLength(bodyBytes.length);

		byte[] headBytes = Head.head2Bytes(head);

		ByteBuf msg = Unpooled.buffer(headBytes.length + bodyBytes.length);

		msg.writeBytes(headBytes);
		msg.writeBytes(bodyBytes);

		nettyChannel.writeAndFlush(msg);

		return future;
	}

	private long generateInvokeId() {
		return ThreadLocalRandom.current().nextLong();
	}

}
