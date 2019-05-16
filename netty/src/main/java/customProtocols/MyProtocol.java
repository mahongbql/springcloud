package customProtocols;

import lombok.Data;

import java.util.Arrays;

/**
 * @author mahongbin
 * @date 2019/5/16 13:35
 * @Description 自定义协议封装类
 */
@Data
public class MyProtocol {

    /**
     * 消息的开头信息标志
     */
    private int head_data = ConstantValue.HEAD_DATA;

    /**
     * 消息的长度
     */
    private int contentLength;

    /**
     * 消息的内容
     */
    private byte[] content;

    /**
     * 用于初始化
     *
     * @param contentLength
     * @param content
     */
    public MyProtocol(int contentLength, byte[] content) {
        this.contentLength = contentLength;
        this.content = content;
    }

    @Override
    public String toString() {
        return "MyProtocol [head_data=" + head_data + ", contentLength="
                + contentLength + ", content=" + Arrays.toString(content) + "]";
    }
}
