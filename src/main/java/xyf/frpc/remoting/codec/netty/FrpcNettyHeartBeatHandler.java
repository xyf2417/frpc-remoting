package xyf.frpc.remoting.codec.netty;

import io.netty.channel.Channel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xyf.frpc.remoting.Constants;
import xyf.frpc.remoting.client.netty.NettyReferenceClient;
import xyf.frpc.remoting.config.AbstractResultHandler;
import xyf.frpc.remoting.handler.ResultHandler;
import xyf.frpc.remoting.server.netty.NettyProviderServer;
import xyf.frpc.rpc.data.Head;
import xyf.frpc.rpc.data.Request;
import xyf.frpc.rpc.data.RequestBody;
import xyf.frpc.rpc.data.Response;
import xyf.frpc.rpc.data.ResponseBody;


public class FrpcNettyHeartBeatHandler extends AbstractResultHandler {
	private static final Log logger = LogFactory
			.getLog(FrpcNettyHeartBeatHandler.class);
	private ResultHandler handler;
	
	public FrpcNettyHeartBeatHandler(ResultHandler handler) {
		this.handler = handler;
		this.setIsProvider(handler.isProvider());
		this.setAttachment(handler.getAttachment());
	}

	public Object received(Object msg, Channel nettyChannel) {
		
		if(msg instanceof Request) {
			Request request = (Request)msg;
			if(request.getBody().getEventType() == RequestBody.EventType.HEART_BEAT) {
				return processHeartBeatRequest(request, nettyChannel);
			}
		} else if(msg instanceof Response) {
			Response response = (Response)msg;
			if(response.getBody().getEventType() == ResponseBody.EventType.HEART_BEAT) {
				return processHeartBeatResponse(response, nettyChannel);
			}
		}
		return handler.received(msg, nettyChannel);
	}
	
	private Object processHeartBeatRequest(Request request, Channel nettyChannel) {
		Head head = new Head();
		head.setMagic(Head.MAGIC_NUMBER);
		
		ResponseBody body = new ResponseBody();
		body.setEventType(ResponseBody.EventType.HEART_BEAT);
		
		Response response = new Response();
		response.setHead(head);
		response.setBody(body);
		
		nettyChannel.writeAndFlush(response);
		return null;
	}
	
	private Object processHeartBeatResponse(Response response, Channel nettyChannel) {
		if(isProvider()) {
			String key = getChannelKey(nettyChannel);
			NettyProviderServer server = (NettyProviderServer) handler.getAttachment();
			server.setChannelAttribute(key, Constants.HEART_BEAT_LAST_RECV_TIME_KEY, System.currentTimeMillis());
			logger.info("frpc: server receive heart response from " + key);
		} else {
			NettyReferenceClient client = (NettyReferenceClient) handler.getAttachment();
			client.getChannel().addAttribute(Constants.HEART_BEAT_LAST_RECV_TIME_KEY, System.currentTimeMillis());
			logger.info("frpc: reference receive heart beat response from " +  client.getChannel().getNettyChannel());
		}
		return null;
	}
	
	private String getChannelKey(Channel nettyChannel) {
		String key = nettyChannel.remoteAddress().toString();
		key = key.replace("/", "");
		return key;
	}
}
