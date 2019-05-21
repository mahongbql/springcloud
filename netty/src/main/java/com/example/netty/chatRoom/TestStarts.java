package com.example.netty.chatRoom;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author mahongbin
 * @date 2019/5/21 16:20
 * @Description
 */
@Component
public class TestStarts implements InitializingBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        int port = 8887;

        ChatServer server = new ChatServer(port);
        server.start();
        System.out.println("项目启动中：房间已开启");
        System.out.println("等待客户端接入的端口号: " + server.getPort());

    }
}
