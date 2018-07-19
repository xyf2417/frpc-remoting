package xyf.frpc.remoting.codec.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import xyf.frpc.rpc.data.Head;
import xyf.frpc.rpc.data.Request;
import xyf.frpc.rpc.data.RequestBody;
import xyf.frpc.rpc.data.Response;
import xyf.frpc.rpc.data.ResponseBody;

public class FrpcNettyDecoder extends ByteToMessageDecoder {
	private Head currentHead;
	
	private boolean toReadHead = true;
	
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		while(true) {
			int len = in.readableBytes();
			if(toReadHead) {
				if(len < Head.HEAD_LENGTH) {
					return ; //can't read a entire head from the bytebuf in
				}
				byte [] headBytes = new byte[Head.HEAD_LENGTH];
				in.readBytes(headBytes, 0, Head.HEAD_LENGTH);
				
				Head head = Head.bytes2Head(headBytes);
				
				currentHead = head;
				
				toReadHead = false;
			} else {
				int bodyLen = currentHead.getBodyLength();
				if(len < bodyLen) {
					return ; //can't read a entire body from the bytebuf in
				}
				byte [] bodyBytes = new byte[bodyLen];
				in.readBytes(bodyBytes, 0, bodyLen);
				if(currentHead.getFlag() == Head.REQUEST_FLAG) {
					RequestBody body = JavaSerializableReqRespBodyPack.toRequestBodyObject(bodyBytes);
					Request request = new Request();
					request.setHead(currentHead);
					request.setBody(body);
					out.add(request);
				}
				else {
					ResponseBody body = JavaSerializableReqRespBodyPack.toResponseBodyObject(bodyBytes);
					Response response = new Response();
					response.setHead(currentHead);
					response.setBody(body);
					out.add(response);
				}
				toReadHead = true;
			}
		}
	}
}
