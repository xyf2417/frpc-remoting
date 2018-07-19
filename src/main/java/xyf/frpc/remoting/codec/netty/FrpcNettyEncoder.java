package xyf.frpc.remoting.codec.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xyf.frpc.rpc.data.Head;
import xyf.frpc.rpc.data.Request;
import xyf.frpc.rpc.data.RequestBody;
import xyf.frpc.rpc.data.Response;
import xyf.frpc.rpc.data.ResponseBody;

public class FrpcNettyEncoder extends MessageToByteEncoder {
	private final static Log logger = LogFactory
			.getLog(FrpcNettyReferenceEncoder.class);
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)
			throws Exception {
		if(msg instanceof Request) {
			Request request = (Request)msg;
			Head head = request.getHead();
			RequestBody body = request.getBody();
			
			byte[] bodyBytes = null;
			try {
				bodyBytes = JavaSerializableReqRespBodyPack.toArray(body);
			} catch (IOException e) {
				logger.error("frpc: serializable request error");
			}
	
			head.setBodyLength(bodyBytes.length);
	
			byte[] headBytes = Head.head2Bytes(head);
	
			out.writeBytes(headBytes);
			out.writeBytes(bodyBytes);
		} else {
			Response response = (Response)msg;
			Head head = response.getHead();

			ResponseBody body = response.getBody();


			byte[] bodyBytes = null;
			try {
				bodyBytes = JavaSerializableReqRespBodyPack.toArray(body);
			} catch (IOException e) {
				e.printStackTrace();
			}

			head.setBodyLength(bodyBytes.length);

			byte[] headBytes = Head.head2Bytes(head);

			out.writeBytes(headBytes);
			out.writeBytes(bodyBytes);
		}
	}

}
