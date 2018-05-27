package xyf.frpc.remoting.codec.netty;

public class NettyCodecByteBuf {
	/**
	 * The default capacity of the Buf is 16K
	 */
	public final static int DEFAULT_BUF_CAPACITY = 1024 * 16;
	
	private byte [] buf;
	
	private int currentSize;
	
	
	public NettyCodecByteBuf(int capacity) {
		buf = new byte[capacity];
	}
	
	public NettyCodecByteBuf() {
		buf = new byte[DEFAULT_BUF_CAPACITY];
	}
	
	/**
	 * 
	 */
	private void adjust(int start)
	{
		if(currentSize == buf.length || currentSize == 0)
		{
			return;
		}
		System.arraycopy(buf, start, buf, 0, currentSize);
	}
	
	public void write(byte [] bytes) {
		capacityCheck(bytes.length);
		System.arraycopy(bytes, 0, buf, currentSize, bytes.length);
		currentSize += bytes.length;
	}
	
	private void capacityCheck(int toWriteLength) {
		if(buf.length - currentSize < toWriteLength) {
			throw new RuntimeException("Buf overflow");
		}
	}
	
	public boolean canWrite(int length) {
		return buf.length - currentSize - length >= 0;
	}
	
	/**
	 * 
	 * @return
	 */
	public int size() {
		return currentSize;
	}
	
	public byte[] read(int length) {
		if(length > currentSize)
		{
			throw new RuntimeException("Not enough byte to poll");
		}
		
		byte [] result = new byte[length];
		
		System.arraycopy(buf, 0, result, 0, length);
		
		currentSize = currentSize - length;
		
		adjust(length);
		
		return result;
		
	}
	
}
