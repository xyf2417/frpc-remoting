package xyf.frpc.remoting.codec.netty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import xyf.frpc.remoting.data.RequestBody;
import xyf.frpc.remoting.data.ResponseBody;

public class JavaSerializableReqRespBodyPack {
	
	public static RequestBody toRequestBodyObject(byte [] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ObjectInputStream oin = new ObjectInputStream(in);
		RequestBody body = (RequestBody)oin.readObject();
		return body;
	}
	
	public static byte[] toArray(Object body) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oout = new ObjectOutputStream(out);
		oout.writeObject(body);
		return out.toByteArray();
	}
	
	public static ResponseBody toResponseBodyObject(byte [] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ObjectInputStream oin = new ObjectInputStream(in);
		ResponseBody body = (ResponseBody)oin.readObject();
		return body;
	}
	
	
}
