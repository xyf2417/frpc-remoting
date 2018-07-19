package xyf.frpc.remoting.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xyf.frpc.remoting.Constants;
import xyf.frpc.remoting.HeartBeatTask;
import xyf.frpc.remoting.RpcChannel;
import xyf.frpc.remoting.client.ReferenceClient;
import xyf.frpc.remoting.codec.netty.FrpcNettyDecoder;
import xyf.frpc.remoting.codec.netty.FrpcNettyEncoder;
import xyf.frpc.remoting.codec.netty.FrpcNettyHeartBeatHandler;
import xyf.frpc.remoting.codec.netty.FrpcNettyReferenceHandler;
import xyf.frpc.remoting.handler.ResultHandler;
import xyf.frpc.remoting.netty.NettyRpcChannel;
import xyf.frpc.rpc.Invocation;
import xyf.frpc.rpc.ResponseFuture;
import xyf.frpc.rpc.RpcException;
import xyf.frpc.rpc.data.Head;
import xyf.frpc.rpc.data.Request;
import xyf.frpc.rpc.data.RequestBody;

public class NettyReferenceClient implements ReferenceClient {

	private final static Log logger = LogFactory
			.getLog(NettyReferenceClient.class);
	
	/**
	 * scheduled for heartbeat
	 */
	ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1,
			new HeartBeatTask.HeartBeatThreadFactory());

	private ResultHandler resultHandler;

	private ChannelFuture nettyChannelFuture;

	private RpcChannel rpcChannel;

	private EventLoopGroup workerGroup;

	@SuppressWarnings("unchecked")
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
									new FrpcNettyDecoder());
							ch.pipeline().addLast(
									new FrpcNettyEncoder());
							ch.pipeline()
									.addLast(
											new FrpcNettyReferenceHandler(
													new FrpcNettyHeartBeatHandler(resultHandler)));
						}
					});
			nettyChannelFuture = b.connect(ip, port).sync();
			nettyChannelFuture.addListener(new GenericFutureListener() {
				public void operationComplete(Future future) throws Exception {
					startHeartBeatTask();
				}
				
			});
			Channel nettyChannel = nettyChannelFuture.channel();
			rpcChannel = new NettyRpcChannel(nettyChannel);

		} catch (Exception e) {
			throw new RpcException(e.getMessage());
		} finally {
			// no op;
		}

	}
	
	private void startHeartBeatTask() {
		HeartBeatTask heartBeatTask = new HeartBeatTask() {
			@Override
			public void run() {
				logger.info("frpc: reference heart beat run");
				long now = System.currentTimeMillis();
				long lastRecvTime = (Long) rpcChannel
						.getAttribute(Constants.HEART_BEAT_LAST_RECV_TIME_KEY);
//				if ((!((String) rpcChannel
//						.getAttribute(Constants.FIRST_HEART_BEAT_KEY))
//						.equals("true"))
//						&& ((now - lastRecvTime) > HeartBeatTask.DEFAULT_LOST_THRESHOLD)) {
//					rpcChannel.addAttribute(Constants.FIRST_HEART_BEAT_KEY, "false");
//					rpcChannel.getNettyChannel().close();
//					logger.info("frpc: reference lost connection with service bacause of heart timeout");
//				}
//				else {
					if(((String) rpcChannel
							.getAttribute(Constants.FIRST_HEART_BEAT_KEY))
							.equals("true")) {
							rpcChannel.addAttribute(Constants.FIRST_HEART_BEAT_KEY, "false");
						}
					Head head = new Head();
					head.setMagic(Head.MAGIC_NUMBER);
					head.setFlag(Head.REQUEST_FLAG);
					
					RequestBody body = new RequestBody();
					body.setEventType(RequestBody.EventType.HEART_BEAT);
					
					Request request = new Request();
					request.setHead(head);
					request.setBody(body);
					logger.info("frpc: reference send heart beat - " + rpcChannel.getNettyChannel());
					rpcChannel.getNettyChannel().writeAndFlush(request);
				//}
			}//run
		};
		scheduled.scheduleWithFixedDelay(heartBeatTask,
				HeartBeatTask.DEFAULT_HEART_BEAT_INTERVAL,
				HeartBeatTask.DEFAULT_HEART_BEAT_INTERVAL,
				HeartBeatTask.HEART_TIME_UNIT);
	}
	
	public RpcChannel getChannel() {
		return rpcChannel;
	}

	public void setResultHandler(ResultHandler resultHandler) {
		this.resultHandler = resultHandler;
		resultHandler.setIsProvider(false);
		resultHandler.setAttachment(this);
	}

	public ResponseFuture request(Invocation invocation) throws RpcException {

		long invokeId = RequestBody.nextInvokeId();

		invocation.setInvokeId(invokeId);

		ResponseFuture future = new ResponseFuture(invocation);

		Head head = new Head();
		head.setMagic(Head.MAGIC_NUMBER);
		head.setFlag(Head.REQUEST_FLAG);

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

		ChannelFuture f = rpcChannel.getNettyChannel().writeAndFlush(request);
		try{
			f.get();
		}
		catch(Throwable t) {
			throw new RpcException(t.getMessage());
		}
		
		return future;
	}
}
