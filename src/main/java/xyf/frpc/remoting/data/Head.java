package xyf.frpc.remoting.data;

import xyf.frpc.remoting.codec.netty.ByteUtil;

/**
 * +---------+--------------------------------------+-------------------+ |
 * magic | flag|status| invokeId | bodyLegth | | | | | | |
 * +--------------------------------------------------------------------+ | | |
 * | | | | 2 | 1 | 1 | 8 | 4 | | | | | | |
 * +---------+-----+------+-------------------------+-------------------+
 * 
 * @author xyf
 * 
 */
public class Head {
	public static final short MAGIC = 24256;
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

	public static Head bytes2Head(byte[] bytes) {
		if (bytes.length != HEAD_LENGTH) {
			throw new RuntimeException(
					"The length of bytes array can't not resolve to a Head");
		}
		Head head = new Head();
		byte[] temp = new byte[2];
		System.arraycopy(bytes, 0, temp, 0, 2);
		head.setMagic(ByteUtil.bytes2short(temp));
		head.setFlag(bytes[2]);
		head.setStatus(bytes[3]);
		temp = new byte[8];
		System.arraycopy(bytes, 4, temp, 0, 8);
		head.setInvokeId(ByteUtil.bytes2long(temp));
		temp = new byte[4];
		System.arraycopy(bytes, 12, temp, 0, 4);
		head.setBodyLength(ByteUtil.bytes2int(temp));
		return head;
	}

	public String toString() {
		StringBuilder res = new StringBuilder();
		res.append("Head(");
		res.append("magic=" + magic + ",flag=" + flag + ",status=" + status);
		res.append(",invokeId=" + invokeId);
		res.append(",bodyLength=" + bodyLength + ")");
		return res.toString();
	}

	public static byte[] head2Bytes(Head head) {
		byte[] bytes = new byte[HEAD_LENGTH];
		System.arraycopy(ByteUtil.short2bytes(head.getMagic()), 0, bytes, 0, 2);
		bytes[2] = head.getFlag();
		bytes[3] = head.getStatus();
		System.arraycopy(ByteUtil.long2bytes(head.getInvokeId()), 0, bytes, 4,
				8);
		System.arraycopy(ByteUtil.int2bytes(head.getBodyLength()), 0, bytes,
				12, 4);
		return bytes;
	}
}
