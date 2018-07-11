package xyf.frpc.remoting.codec.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import xyf.frpc.rpc.data.Head;
import xyf.frpc.rpc.data.Response;
import xyf.frpc.rpc.data.ResponseBody;

public class FrpcNettyReferenceDecoder extends ByteToMessageDecoder {
	private Response currentResponse;
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
				currentResponse = new Response();
				Head head = Head.bytes2Head(headBytes);
				currentResponse.setHead(head);
				toReadHead = false;
			} else {
				int bodyLen = currentResponse.getHead().getBodyLength();
				if(len < bodyLen) {
					return ; //can't read a entire body from the bytebuf in
				}
				byte [] bodyBytes = new byte[bodyLen];
				in.readBytes(bodyBytes, 0, bodyLen);
				ResponseBody body = JavaSerializableReqRespBodyPack
						.toResponseBodyObject(bodyBytes);
				currentResponse.setBody(body);
				out.add(currentResponse);
				toReadHead = true;
			}
		}
		
	}
}
