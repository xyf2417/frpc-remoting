package xyf.frpc.remoting.codec.netty;

import java.io.IOException;
import java.util.Arrays;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import xyf.frpc.config.Application;
import xyf.frpc.config.Provider;
import xyf.frpc.remoting.codec.Decoder;
import xyf.frpc.remoting.codec.Encoder;
import xyf.frpc.remoting.data.Head;
import xyf.frpc.remoting.data.Request;
import xyf.frpc.remoting.data.RequestBody;
import xyf.frpc.remoting.data.Response;
import xyf.frpc.remoting.data.ResponseBody;
import xyf.frpc.rpc.Invocation;
import xyf.frpc.rpc.Invoker;
import xyf.frpc.rpc.MethodInvocation;
import xyf.frpc.rpc.Result;
import xyf.frpc.rpc.ResultStatus;
import xyf.frpc.rpc.RpcResult;

public class NettyRequestCoder implements Decoder, Encoder {
	
	private NettyCodecByteBuf buffer = new NettyCodecByteBuf();
	
	private Request currentRequest;

	private boolean toReadHead = true;
	
	public Object decode(Object msg, Object out){
		ChannelHandlerContext ctx = null;
		try {
			ByteBuf buf= (ByteBuf)msg;
			byte [] bytes = new byte[buf.readableBytes()];
			buf.readBytes(bytes);
			ctx = (ChannelHandlerContext)out;
			if(!buffer.canWrite(bytes.length)) {
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
				
				currentRequest = new Request();
				Head head = Head.bytes2Head(headBytes);
				currentRequest.setHead(head);
				
				toReadHead = false;
			} else {
				if(buffer.size() < currentRequest.getHead().getBodyLength()) {
					System.out.println("body break");
					break;
				}
				byte [] bytes = buffer.read(currentRequest.getHead().getBodyLength());
				try {
					currentRequest.setBody(JavaSerializableReqRespBodyPack.toRequestBodyObject(bytes));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				toReadHead = true;
				ByteBuf outmsg = null;
				
				//Construct the response head
				Head head = new Head();
				head.setMagic(Head.MAGIC);
				head.setInvokeId(currentRequest.getHead().getInvokeId());
				
				ResponseBody body = new ResponseBody();
				
				/*Application application = Application.getApplication();
				
				Invoker invoker = application.getInovoker(currentRequest.getBody().getInterfaceFullName());
				
				
				
				if(invoker == null) {
					Result result = new RpcResult();
					result.setStatus(ResultStatus.ERROR);
					body.setReturnValue(result);
					System.out.println("-----NettyRequestCoder can't find the invoke :" + body);
				} 
				else {
					Invocation invocation = new MethodInvocation();
					invocation.setInterfaceFullName(currentRequest.getBody().getInterfaceFullName());
					invocation.setMethodName(currentRequest.getBody().getMethodName());
					invocation.setArguments(currentRequest.getBody().getArguments());
					invocation.setParameterTypes(currentRequest.getBody().getParameterTypes());
					
					Result result = invoker.invoke(invocation);

					//construct the response body
					body.setReturnValue(result);
					System.out.println("-----NettyRequestCoder find the invoke :" + body);
				}*/
				
				byte[] bodyBytes = null;
				try {
					bodyBytes = JavaSerializableReqRespBodyPack.toArray(body);
				} catch (IOException e) {
					e.printStackTrace();
				}
				head.setBodyLength(bodyBytes.length);
				
				
				byte [] headBytes = Head.head2Bytes(head);
				
				outmsg = Unpooled.buffer(headBytes.length + bodyBytes.length);
				
				outmsg.writeBytes(headBytes);
				outmsg.writeBytes(bodyBytes);
				
				ctx.writeAndFlush(outmsg);
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
