package xyf.frpc.remoting.codec.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import xyf.frpc.remoting.codec.Decoder;
import xyf.frpc.remoting.codec.Encoder;
import xyf.frpc.remoting.data.Head;
import xyf.frpc.remoting.data.Request;
import xyf.frpc.remoting.data.Response;

public class NettyResponseCoder  implements Decoder, Encoder{
	private NettyCodecByteBuf buffer = new NettyCodecByteBuf();
	
	private Response currentResponse;
	
	private boolean toReadHead = true;
	
	public Object decode(Object msg, Object out){
		System.out.println("client decode 1");
		ChannelHandlerContext ctx = null;
		try {
			ByteBuf buf= (ByteBuf)msg;
			byte [] bytes = new byte[buf.readableBytes()];
			buf.readBytes(bytes);
			System.out.println("client decode 2");
			ctx = (ChannelHandlerContext)out;
			if(!buffer.canWrite(bytes.length)) {
				System.out.println("exception");
				throw new RuntimeException("buffer overflow when decode");
			}
			buffer.write(bytes);
			System.out.println("client decode 3");
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("client decode 4");
		}

		while(true) {
			if(toReadHead)
			{
				System.out.println("client decode 5");
				if(buffer.size() < Head.HEAD_LENGTH) {
					System.out.println("head break");
					break;
				}
				System.out.println("client decode 6");
				//decode Head
				byte [] headBytes = buffer.read(Head.HEAD_LENGTH);
				
				System.out.println("client decode 7");
				currentResponse = new Response();
				Head head = Head.bytes2Head(headBytes);
				currentResponse.setHead(head);
				System.out.println("client decode 8");
				
				toReadHead = false;
			} else {
				if(buffer.size() < currentResponse.getHead().getBodyLength()) {
					System.out.println("client decode 9");
					break;
				}
				byte [] bytes = buffer.read(currentResponse.getHead().getBodyLength());
				System.out.println("client decode 10");
				try {
					currentResponse.setBody(JavaSerializableReqRespBodyPack.toResponseBodyObject(bytes));
					System.out.println("client decode 11");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("client decode 12");
				}
				
				toReadHead = true;
				System.out.println("client decode 14");
				System.out.println(currentResponse);
				
			}
		}
		return null;
	}

	public Object encode(Object msg, Object out) {
		Request request = (Request)msg;
		ChannelHandlerContext ctx = (ChannelHandlerContext)out;
		
		byte [] head = Head.head2Bytes(request.getHead());
		ctx.writeAndFlush(head);
		return null;
	}
}
