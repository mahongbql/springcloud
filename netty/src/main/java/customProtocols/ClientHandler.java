package customProtocols;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;

/**
 * @author mahongbin
 * @date 2019/5/16 14:01
 * @Description
 */
public class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    // 客户端与服务端，连接成功的售后
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 100; i++) {
            byte[] content = "I am client ...".getBytes(Charset.forName("utf-8"));
            int contentLength = content.length;
            MyProtocol protocol = new MyProtocol(contentLength, content);
            ctx.writeAndFlush(protocol);
        }
    }

    // 只是读数据，没有写数据的话
    // 需要自己手动的释放的消息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        try {
            // 用于获取客户端发来的数据信息
            MyProtocol myProtocol = (MyProtocol) msg;
            System.out.println("Client 接受的客户端的信息 :" + new String(myProtocol.getContent(), "utf-8"));

        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        ctx.close();
    }

}
