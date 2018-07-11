package xyf.frpc.remoting.codec.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import xyf.frpc.rpc.data.Head;
import xyf.frpc.rpc.data.Request;
import xyf.frpc.rpc.data.RequestBody;

public class FrpcNettyServiceDecoder extends ByteToMessageDecoder {
	private Request currentRequest;
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
				currentRequest = new Request();
				Head head = Head.bytes2Head(headBytes);
				currentRequest.setHead(head);
				toReadHead = false;
			} else {
				int bodyLen = currentRequest.getHead().getBodyLength();
				if(len < bodyLen) {
					return ; //can't read a entire body from the bytebuf in
				}
				byte [] bodyBytes = new byte[bodyLen];
				in.readBytes(bodyBytes, 0, bodyLen);
				RequestBody body = JavaSerializableReqRespBodyPack.toRequestBodyObject(bodyBytes);
				currentRequest.setBody(body);
				out.add(currentRequest);
				toReadHead = true;
			}
		}
	}
}
