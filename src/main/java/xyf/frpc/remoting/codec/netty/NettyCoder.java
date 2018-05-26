package xyf.frpc.remoting.codec.netty;

import xyf.frpc.remoting.codec.Decoder;
import xyf.frpc.remoting.codec.Encoder;

public class NettyCoder implements Decoder, Encoder {
	
	private NettyCodecByteBuf deBuf = new NettyCodecByteBuf();
	
	/**
	 * 用于记录下次要读取的是头部还是消息体
	 */
	private boolean toReadHead = true;
	
	public Object decode(Object msg) {
		
		return null;
	}

	public Object encode(Object msg) {
		// TODO Auto-generated method stub
		return null;
	}

}
