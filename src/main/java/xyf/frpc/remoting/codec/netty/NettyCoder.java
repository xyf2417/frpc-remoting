package xyf.frpc.remoting.codec.netty;

import io.netty.channel.ChannelHandlerContext;
import xyf.frpc.remoting.codec.Decoder;
import xyf.frpc.remoting.codec.Encoder;
import xyf.frpc.remoting.data.Head;
import xyf.frpc.remoting.data.Request;
import xyf.frpc.remoting.data.RequestBody;

public class NettyCoder implements Decoder, Encoder {

	private NettyCodecByteBuf buffer = new NettyCodecByteBuf();

	private Request currentRequest;

	private boolean toReadHead = true;

	public Object decode(Object msg, Object out) {
		byte[] bytes = (byte[]) msg;
		ChannelHandlerContext ctx = (ChannelHandlerContext) out;

		if (!buffer.canWrite(bytes.length)) {
			throw new RuntimeException("buffer overflow when decode");
		}
		buffer.write(bytes);

		while (true) {
			if (toReadHead) {
				if (buffer.size() < Head.HEAD_LENGTH) {
					break;
				}
				// decode Head
				byte[] headBytes = buffer.read(Head.HEAD_LENGTH);

				currentRequest = new Request();
				currentRequest.setHead(Head.bytes2Head(headBytes));

				toReadHead = false;
			} else {
				if (buffer.size() < currentRequest.getHead().getBodyLength()) {
					break;
				}
				RequestBody body = new RequestBody();
				currentRequest.setBody(body);

				toReadHead = true;

				ctx.fireChannelRead(currentRequest);
			}
		}
		return null;
	}

	public Object encode(Object msg, Object out) {
		Request request = (Request) msg;
		ChannelHandlerContext ctx = (ChannelHandlerContext) out;

		byte[] head = Head.head2Bytes(request.getHead());
		ctx.writeAndFlush(head);
		return null;
	}

}
