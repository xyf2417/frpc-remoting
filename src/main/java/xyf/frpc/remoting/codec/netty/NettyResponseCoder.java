package xyf.frpc.remoting.codec.netty;

import java.io.IOException;

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
	
	
	/**
	 * ���ڼ�¼�´�Ҫ��ȡ����ͷ��������Ϣ��
	 */
	private boolean toReadHead = true;
	
	public Object decode(Object msg, Object out){
		ChannelHandlerContext ctx = null;
		try {
			ByteBuf buf= (ByteBuf)msg;
			byte [] bytes = new byte[buf.readableBytes()];
			buf.readBytes(bytes);
			ctx = (ChannelHandlerContext)out;
			if(!buffer.canWrite(bytes.length)) {
				System.out.println("exception");
				throw new RuntimeException("buffer overflow when decode");
			}
			buffer.write(bytes);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		
		
		while(true) {
			if(toReadHead)
			{
				if(buffer.size() < Head.HEAD_LENGTH) {
					System.out.println("head break");
					break;
				}
				//decode Head
				byte [] headBytes = buffer.read(Head.HEAD_LENGTH);
				
				currentResponse = new Response();
				Head head = Head.bytes2Head(headBytes);
				currentResponse.setHead(head);
				
				toReadHead = false;
			} else {
				if(buffer.size() < currentResponse.getHead().getBodyLength()) {
					System.out.println("body break");
					break;
				}
				byte [] bytes = buffer.read(currentResponse.getHead().getBodyLength());
				try {
					currentResponse.setBody(JavaSerializableReqRespBodyPack.toResponseBodyObject(bytes));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				toReadHead = true;
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
