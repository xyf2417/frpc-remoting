package xyf.frpc.remoting.data;

public class RequestHead {
public static final int HEAD_LENGTH = 16;
	
	private short magic;
	
	private byte flag;
	
	private byte status;
	
	private long invokeId;
	
	private int bodyLength;
	
	
	public short getMagic() {
		return magic;
	}

	public void setMagic(short magic) {
		this.magic = magic;
	}

	public byte getFlag() {
		return flag;
	}

	public void setFlag(byte flag) {
		this.flag = flag;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public long getInvokeId() {
		return invokeId;
	}

	public void setInvokeId(long invokeId) {
		this.invokeId = invokeId;
	}

	public int getBodyLength() {
		return bodyLength;
	}

	public void setBodyLength(int bodyLength) {
		this.bodyLength = bodyLength;
	}
	
	public RequestHead bytes2RequestHead(byte [] bytes) {
		if(bytes.length != HEAD_LENGTH)
			return null;
		byte [] temp = new byte[2];
		return null;
	}
}
