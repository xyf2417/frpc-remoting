package xyf.frpc.remoting.codec.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.Arrays;

import xyf.frpc.remoting.codec.Decoder;
import xyf.frpc.remoting.codec.Encoder;
import xyf.frpc.remoting.data.Head;
import xyf.frpc.remoting.data.Request;
import xyf.frpc.remoting.data.Response;
import xyf.frpc.remoting.data.ResponseBody;
import xyf.frpc.remoting.handler.ResultHandler;
import xyf.frpc.rpc.Result;
import xyf.frpc.rpc.ResultStatus;
import xyf.frpc.rpc.RpcResult;

public class NettyRequestCoder implements Decoder, Encoder {

	private ResultHandler resultHandler;

	private NettyCodecByteBuf buffer = new NettyCodecByteBuf();

	private Request currentRequest;

	private boolean toReadHead = true;

	public NettyRequestCoder(ResultHandler resultHandler) {
		this.resultHandler = resultHandler;
	}

	public Object decode(Object msg, Object out) {
		ChannelHandlerContext ctx = null;
		try {
			ByteBuf buf = (ByteBuf) msg;
			byte[] bytes = new byte[buf.readableBytes()];
			buf.readBytes(bytes);
			ctx = (ChannelHandlerContext) out;
			if (!buffer.canWrite(bytes.length)) {
				throw new RuntimeException("buffer overflow when decode");
			}
			buffer.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}

		while (true) {
			if (toReadHead) {
				if (buffer.size() < Head.HEAD_LENGTH) {
					break;
				}
				// decode Head
				byte[] headBytes = buffer.read(Head.HEAD_LENGTH);

				currentRequest = new Request();
				Head head = Head.bytes2Head(headBytes);
				currentRequest.setHead(head);

				toReadHead = false;
			} else {
				if (buffer.size() < currentRequest.getHead().getBodyLength()) {
					break;
				}
				byte[] bytes = buffer.read(currentRequest.getHead()
						.getBodyLength());
				try {
					currentRequest.setBody(JavaSerializableReqRespBodyPack
							.toRequestBodyObject(bytes));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				toReadHead = true;

				Response response = (Response) resultHandler
						.received(currentRequest);
				
				ByteBuf outmsg = null;

				// Construct the response head
				Head head = response.getHead();

				ResponseBody body = response.getBody();

				Result result = new RpcResult();
				result.setStatus(ResultStatus.NORMAL);
				result.setValue(body.getReturnValue());
				
				body.setReturnValue(result);

				byte[] bodyBytes = null;
				try {
					bodyBytes = JavaSerializableReqRespBodyPack.toArray(body);
				} catch (IOException e) {
					e.printStackTrace();
				}

				head.setBodyLength(bodyBytes.length);

				byte[] headBytes = Head.head2Bytes(head);

				outmsg = Unpooled.buffer(headBytes.length + bodyBytes.length);

				outmsg.writeBytes(headBytes);
				outmsg.writeBytes(bodyBytes);

				ctx.writeAndFlush(outmsg);
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

	public void setResultHandler(ResultHandler resultHandler) {
		this.resultHandler = resultHandler;
	}

}
