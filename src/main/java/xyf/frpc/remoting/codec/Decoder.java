package xyf.frpc.remoting.codec;

public interface Decoder {
	public Object decode(Object msg, Object out);
}
