package com.example.netty.chatRoom;

import org.java_websocket.WebSocket;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author mahongbin
 * @date 2019/5/21 16:01
 * @Description
 */
@RestController
@RequestMapping("/WebSocket")
public class WebSocketController {

    //推送给所有在线用户
    @RequestMapping(value = "v1/sendAllUser", method = RequestMethod.GET)
    public void sendAllUser(@RequestParam String message){
        //推送给所有在线用户
        WebSocketPool.sendMessageToOnlineAllUser(message);
    }

    //推送给在线的指定用户
    @RequestMapping(value = "v1/sendUser", method = RequestMethod.GET)
    public void loginWithDevice(@RequestParam String pushObject,
                                @RequestParam String message){
        //推送的设备对象
        List<String> userlist = null;
        if (pushObject == null || "".equals(pushObject)) {
            System.out.println("推送对象不能为nuul");
        } else {
            userlist = new ArrayList<>(Arrays.asList(pushObject.split(",")));
        }
        for (String user : userlist){
            //根据用户查找推送的连接对象是否在线
            WebSocket webSocketByUser = WebSocketPool.getWebSocketByUser(user);
            if (null != webSocketByUser){
                //推送给在线的指定用户
                WebSocketPool.sendMessageToOnlineUser(webSocketByUser,message);
            }else {
                //未推送成功处理模块
                System.out.println("用户 【"+user+"】 不在线，推送失败 ！ ");
            }
        }
    }

}

