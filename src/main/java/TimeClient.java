
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import xyf.frpc.remoting.data.Head;

public class TimeClient {

	public void connect(int port, String host) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast(new TimeClientHandler());
				}
			});
			ChannelFuture f = b.connect(host, port).sync();
			f.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}
	public static void main(String[] args) throws Exception {
		new TimeClient().connect(8080, "127.0.0.1");
	}

}

class TimeClientHandler extends ChannelInboundHandlerAdapter {
	private int counter;
	private byte [] req;
	public TimeClientHandler() {
		req = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ByteBuf msg = null;
		for(int i = 0; i < 10; i++) {
			Head head = new Head();
			head.setMagic(Head.MAGIC);
			head.setInvokeId(10*i);
			head.setBodyLength(2*i);
			byte [] bytes = Head.head2Bytes(head);
			msg = Unpooled.buffer(bytes.length);
			msg.writeBytes(bytes);
			ctx.writeAndFlush(msg);
			System.out.println("client msg sent " + i);
		}
	}
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String body = (String)msg;
	
		System.out.println("Now is : " + body + " ; the counter is : " + ++counter);
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	@Override 
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
	}
}
