package customProtocols;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mahongbin
 * @date 2019/5/16 13:58
 * @Description
 */
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {
    public static Map<String, Channel> map = new ConcurrentHashMap();

    // 用于获取客户端发送的信息
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        // 用于获取客户端发来的数据信息
        MyProtocol myProtocol = (MyProtocol) msg;

        /**
         * 用户要发送的消息、用户姓名、发送到用户的id
         * message、userId、userName、toUserId
         */
        JSONObject json = JSON.parseObject(new String(myProtocol.getContent(), "utf-8"));
        //发送的消息
        String message = json.getString("message");

        Channel inComing = ctx.channel();

        String userId = json.getString("userId");

        //发消息的用户名

        String userName = json.getString("userName");

        JSONObject jsonNew = null;
        if (!map.containsKey(userId)) {
            jsonNew = new JSONObject();
            jsonNew.put("message", "用户【" + userName + "】加入房间");
            byte[] content = jsonNew.toString().getBytes(Charset.forName("utf-8"));
            MyProtocol protocol = new MyProtocol(content.length, content);
            for (Map.Entry<String, Channel> m : map.entrySet()) {
                Channel channel = m.getValue();
                channel.writeAndFlush(protocol);
            }
            //将新连接用户保存
            map.put(userId, inComing);
            System.out.println("用户【" + userName + "】加入房间");
            return;
        }

        String toUserId = json.getString("toUserId");
        Channel to_channel = map.get(toUserId);

        String str = null;
        JSONObject jsonObject = new JSONObject();
        if (null != to_channel) {
            jsonObject.put("userName", userName);
            jsonObject.put("message", message);
            str = jsonObject.toString();

            MyProtocol response = new MyProtocol(str.getBytes().length,
                    str.getBytes());
            // 当服务端完成写操作后，关闭与客户端的连接
            to_channel.writeAndFlush(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        // cause.printStackTrace();
        ctx.close();
    }
}
