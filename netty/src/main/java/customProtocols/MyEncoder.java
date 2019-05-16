package customProtocols;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author mahongbin
 * @date 2019/5/16 13:40
 * @Description 自定义协议编码器
 */
public class MyEncoder extends MessageToByteEncoder<MyProtocol> {

    /**
     * 自己定义的协议
     * 数据包格式
     *
     * 1.协议开始标志head_data，为int类型的数据，16进制表示为0x76
     * 2.传输数据的长度contentLength，int类型
     * 3.传输的数据
     *
     * @param channelHandlerContext
     * @param myProtocol
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MyProtocol myProtocol, ByteBuf byteBuf) throws Exception {
        //写入消息的开头的信息标志（int类型）
        byteBuf.writeInt(myProtocol.getHead_data());
        //写入消息的长度（int类型）
        byteBuf.writeInt(myProtocol.getContentLength());
        //写入消息的内容
        byteBuf.writeBytes(myProtocol.getContent());
    }
}
