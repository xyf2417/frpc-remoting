package xyf.frpc.remoting.codec.netty;

import xyf.frpc.remoting.codec.Decoder;
import xyf.frpc.remoting.codec.Encoder;

public class NettyCoder implements Decoder, Encoder {
	
	private NettyCodecByteBuf deBuf = new NettyCodecByteBuf();
	
	/**
	 * ���ڼ�¼�´�Ҫ��ȡ����ͷ��������Ϣ��
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
