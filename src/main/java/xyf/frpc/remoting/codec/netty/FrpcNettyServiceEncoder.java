package xyf.frpc.remoting.codec.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

import xyf.frpc.rpc.data.Head;
import xyf.frpc.rpc.data.Response;
import xyf.frpc.rpc.data.ResponseBody;

public class FrpcNettyServiceEncoder extends MessageToByteEncoder<Response> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Response response, ByteBuf out)
			throws Exception {
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
