package com.example.netty.chatRoom;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Set;

/**
 * @author mahongbin
 * @date 2019/5/21 15:55
 * @Description
 *
 * 与不同的客户端进行通讯的思路为：
 * Step1：在连接成功的时候，向服务器发送自己的用户名，服务器做用户标记；
 * Step2: 发送消息，格式为“XX@XXX”，@前面表示将要发送的对象，“all”表示群发，@后面表示发送的消息。
 *
 * tomcat的方式需要tomcat 7.x，JEE7的支持，使用这种方式无需别的任何配置，只需服务端一个处理类
 */
public class ChatServer extends WebSocketServer {

    private String username;

    public ChatServer(int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public ChatServer(InetSocketAddress address) {
        super(address);
        System.out.println("地址："+address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {

        System.out.println("连接："+conn);

        sendToAll(conn.getRemoteSocketAddress().getAddress().getHostAddress()
                + " 进入房间 ！");

        System.out.println(conn.getRemoteSocketAddress().getAddress()
                .getHostAddress()
                + " 进入房间 ！");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {

        sendToAll(conn.getRemoteSocketAddress().getAddress().getHostAddress()
                + " 离开房间 ！");

        System.out.println(conn.getRemoteSocketAddress().getAddress()
                .getHostAddress()
                + " 离开房间 ！");

        //触发关闭事件
        userLeave(conn);
    }

    //消息发送
    @Override
    public void onMessage(WebSocket conn, String message) {

        //判断是否是第一次接收的消息
        boolean isfirst = true;

        /*sendToAll("["
                + conn.getRemoteSocketAddress().getAddress().getHostAddress()
                + "]" + message);*/

        System.out.println("["
                + conn.getRemoteSocketAddress().getAddress().getHostAddress()
                + "]" + message);

        //判断是否已在连接池中
        Set<WebSocket> webSockets=WebSocketPool.getAllWebSocket();
        for (WebSocket webSocket : webSockets){
            if (webSocket.equals(conn)){
                isfirst =false;
            }
        }

        if (isfirst) {
            this.username = message;
            //客户端发送消息到服务器是触发事件
            if (message != null){
                //判断用户是否已经在线
                WebSocket webSocketByUser = WebSocketPool.getWebSocketByUser(message);
                if (null == webSocketByUser){
                    //将用户加入连接池-在线
                    this.userJoin(username, conn);
                    System.out.println("用户" + username + "上线,在线人数：" + WebSocketPool.getUserCount());
                }else {
                    WebSocketPool.sendMessageToOnlineUser(conn,"["+username+"] 用户已在线,请您换个用户登录！");
                }
            }
        } else {
            String[] msg = message.split("@", 2);//以@为分隔符把字符串分为xxx和xxxxx两部分,msg[0]表示发送至的用户名，all则表示发给所有人
            if (msg[0].equals("all")) {
                sendToAll(msg[1]);
            } else {
                //指定用户发送消息
                sendMessageToUser(conn,msg[0], msg[1]);
            }
        }
    }

    //异常抛出
    @Override
    public void onError(WebSocket conn, Exception e) {
        e.printStackTrace();
        if (conn != null) {
            conn.close();
        }
    }

    // 发送给所有进入房间的人
    private void sendToAll(String text) {
        Collection<WebSocket> conns = connections();
        synchronized (conns) {
            for (WebSocket client : conns) {
                client.send(text);
            }
        }
    }

    /**
     * 用户下线处理
     * @param conn
     */
    public void userLeave(org.java_websocket.WebSocket conn) {
        String user = WebSocketPool.getUserByKey(conn);
        boolean b = WebSocketPool.removeUser(conn); // 在连接池中移除连接
        if (b) {
            WebSocketPool.sendMessageToOnlineAllUser(user); // 把当前用户从所有在线用户列表中删除
            String leaveMsg = "[系统]" + user + "下线了";
            WebSocketPool.sendMessageToOnlineAllUser(leaveMsg); // 向在线用户发送当前用户退出的信息
        }
    }

    /**
     * 用户上线处理
     * @param user
     * @param conn
     */
    public void userJoin(String user, org.java_websocket.WebSocket conn) {
        WebSocketPool.sendMessageToOnlineAllUser(user); // 把当前用户加入到所有在线用户列表中
        String joinMsg = "[系统]" + user + "上线了！";
        WebSocketPool.sendMessageToOnlineAllUser(joinMsg); // 向所有在线用户推送当前用户上线的消息
        WebSocketPool.addUser(user, conn); // 向连接池添加当前的连接的对象
        WebSocketPool.sendMessageToOnlineUser(conn, WebSocketPool.getOnlineUser().toString());  // 向当前连接发送当前在线用户的列表

    }

    /**
     * 发送消息给指定用户
     * @param currentConnection 当前连接
     * @param user  发送对象
     * @param message 消息
     */
    public void sendMessageToUser(WebSocket currentConnection,String user,String message){
        WebSocket conn = WebSocketPool.getWebSocketByUser(user);  //获取发送对象用户的在线连接
        if (null != conn){
            //向特定在线用户发送消息
            WebSocketPool.sendMessageToOnlineUser(conn,message);
            //同时发送消息给当前用户
            WebSocketPool.sendMessageToOnlineUser(currentConnection,message);
        }else {
            WebSocketPool.sendMessageToOnlineUser(currentConnection,"["+user+"] 用户不在线,请您稍后发送！");
            System.out.println("["+user+"] 用户不在线,请您稍后发送！");
            WebSocketPool.sendMessageToOnlineUser(currentConnection,"当前在线人数："+WebSocketPool.getUserCount()+"  有："+WebSocketPool.getOnlineUser());
        }
    }

}
