package xyf.frpc.remoting.data;

public class Request {
	private Head head;
	private RequestBody body;
	
	public Head getHead() {
		return head;
	}
	public void setHead(Head head) {
		this.head = head;
	}
	public RequestBody getBody() {
		return body;
	}
	public void setBody(RequestBody body) {
		this.body = body;
	}
	
}
