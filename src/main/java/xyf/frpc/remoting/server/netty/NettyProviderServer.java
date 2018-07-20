package xyf.frpc.remoting.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xyf.frpc.remoting.Constants;
import xyf.frpc.remoting.HeartBeatTask;
import xyf.frpc.remoting.RpcChannel;
import xyf.frpc.remoting.codec.netty.FrpcNettyDecoder;
import xyf.frpc.remoting.codec.netty.FrpcNettyEncoder;
import xyf.frpc.remoting.codec.netty.FrpcNettyHeartBeatHandler;
import xyf.frpc.remoting.codec.netty.FrpcNettyServiceHandler;
import xyf.frpc.remoting.handler.ResultHandler;
import xyf.frpc.remoting.server.ProviderServer;
import xyf.frpc.rpc.RpcException;
import xyf.frpc.rpc.data.Head;
import xyf.frpc.rpc.data.Request;
import xyf.frpc.rpc.data.RequestBody;

public class NettyProviderServer implements ProviderServer {

	private static final Log logger = LogFactory
			.getLog(NettyProviderServer.class);

	/**
	 * scheduled for heartbeat
	 */
	ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1,
			new HeartBeatTask.HeartBeatThreadFactory());

	ScheduledFuture<?> heartBeatTimer;

	private ResultHandler resultHandler;
	private ChannelFuture nettyChannelFuture;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;

	private Map<String, RpcChannel> channels = new ConcurrentHashMap<String, RpcChannel>();

	@SuppressWarnings("unchecked")
	public void bind(int port) throws RpcException {

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
			nettyChannelFuture.addListener(new GenericFutureListener() {
				public void operationComplete(Future future) throws Exception {
					startHeartBeatTask();
				}
				
			});
			logger.info("frpc:" + " server is listerning at " + port);
		} catch (Exception e) {
			logger.info("frpc:" + " server bind error " + e.getMessage());
			throw new RpcException(e.getMessage());
		} finally {
			// no op
		}

	}

	private void startHeartBeatTask() {
		HeartBeatTask heartBeatTask = new HeartBeatTask() {
			@Override
			public void run() {
				Collection<RpcChannel> rpcChannels = NettyProviderServer.this.channels
						.values();
				for (RpcChannel rpcChannel : rpcChannels) {
					long now = System.currentTimeMillis();
					long lastRecvTime = (Long) rpcChannel
							.getAttribute(Constants.HEART_BEAT_LAST_RECV_TIME_KEY);
					if ((!((String) rpcChannel
							.getAttribute(Constants.FIRST_HEART_BEAT_KEY))
							.equals("true"))
							&& ((now - lastRecvTime) > HeartBeatTask.DEFAULT_LOST_THRESHOLD)) {
						//channels.remove(getChannelKey(rpcChannel.getNettyChannel()));
						//rpcChannel.getNettyChannel().close();
						logger.info("frpc: service lost connection with reference " + rpcChannel.getNettyChannel() +" bacause of heart timeout");
					}
					else {
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
						rpcChannel.getNettyChannel().writeAndFlush(request);
					}
				}//for
			}//run
		};
		scheduled.scheduleWithFixedDelay(heartBeatTask,
				HeartBeatTask.DEFAULT_HEART_BEAT_INTERVAL,
				HeartBeatTask.DEFAULT_HEART_BEAT_INTERVAL,
				HeartBeatTask.HEART_TIME_UNIT);
	}

	public void setResultHandler(ResultHandler resultHandler) {
		this.resultHandler = resultHandler;
		resultHandler.setIsProvider(true);
		resultHandler.setAttachment(this);
	}

	public ChannelFuture getNettyChannelFuture() {
		return nettyChannelFuture;
	}

	public void setNettyChannelFuture(ChannelFuture nettyChannelFuture) {
		this.nettyChannelFuture = nettyChannelFuture;
	}

	public void addChannel(String key, RpcChannel channel) {
		channels.put(key, channel);
	}
	
	public void setChannelAttribute(String channelKey, String attrKey, Object attrValue) {
		if(channels.containsKey(channelKey)) {
			channels.get(channelKey).addAttribute(attrKey, attrValue);
		}
	}

	public void removeChannel(String key) {
		channels.remove(key);
	}
	
	private String getChannelKey(Channel nettyChannel) {
		String key = nettyChannel.remoteAddress().toString();
		key = key.replace("/", "");
		return key;
	}
}

class ChildChannelHandler extends ChannelInitializer<Channel> {

	private ResultHandler resultHandler;

	public ChildChannelHandler(ResultHandler resultHandler) {
		this.resultHandler = resultHandler;
	}

	@Override
	protected void initChannel(Channel ch) throws Exception {
		ch.pipeline().addLast(new FrpcNettyDecoder());
		ch.pipeline().addLast(new FrpcNettyEncoder());
		ch.pipeline().addLast(
				new FrpcNettyServiceHandler(new FrpcNettyHeartBeatHandler(resultHandler)));
	}
}
