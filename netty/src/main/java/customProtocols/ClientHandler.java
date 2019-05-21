package customProtocols;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
        System.out.println("客户端连接成功");
        JSONObject json = new JSONObject();
        json.put("userName", "李四");
        json.put("userId", "2");
        json.put("message", "hello，帅哥");

        byte[] content = json.toString().getBytes(Charset.forName("utf-8"));
        int contentLength = content.length;
        MyProtocol protocol = new MyProtocol(contentLength, content);
        ctx.writeAndFlush(protocol);
    }

    // 只是读数据，没有写数据的话
    // 需要自己手动的释放的消息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        try {
            // 用于获取客户端发来的数据信息
            MyProtocol myProtocol = (MyProtocol) msg;
            String content = new String(myProtocol.getContent(), "utf-8");
            JSONObject json = JSONObject.parseObject(content);
            String userName = json.getString("userName");
            if (null != userName) {
                System.out.println(json.getString("userName") + " :" + json.getString("message"));
            } else {
                System.out.println(json.getString("message"));
            }


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
