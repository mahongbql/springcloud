package com.example.netty.chatRoom;

import org.java_websocket.WebSocket;

import java.util.*;

/**
 * @author mahongbin
 * @date 2019/5/21 15:38
 * @Description
 */
public class WebSocketPool {
    //连接-用户名
    private static final Map<WebSocket, String> userconnections = new HashMap<>();

    /**
     * 获取用户名
     * @param conn
     * @return
     */
    public static String getUserByKey(WebSocket conn) {
        return userconnections.get(conn);
    }

    /**
     * 获取在线总数
     * @return
     */
    public static int getUserCount() {
        return userconnections.size();
    }

    /**
     * 获取WebSocket
     * @param user
     * @return
     */
    public static WebSocket getWebSocketByUser(String user) {
        Set<WebSocket> keySet = userconnections.keySet();
        synchronized (keySet) {
            for (WebSocket conn : keySet) {
                String cuser = userconnections.get(conn);
                if (cuser.equals(user)) {
                    return conn;
                }
            }
        }
        return null;
    }

    /**
     * 向连接池中添加连接
     * @param user
     * @param conn
     */
    public static void addUser(String user, WebSocket conn) {
        userconnections.put(conn, user); // 添加连接
    }

    /**
     * 获取所有连接池
     */
    public static Set<WebSocket> getAllWebSocket() {
        return userconnections.keySet();
    }

    /**
     * 移除连接池中的连接
     * @param conn
     * @return
     */
    public static boolean removeUser(WebSocket conn) {
        if (userconnections.containsKey(conn)) {
            userconnections.remove(conn); // 移除连接
            return true;
        } else {
            return false;
        }

    }

    /**
     * 获取所有的在线用户
     * @return
     */
    public static Collection<String> getOnlineUser() {
        List<String> setUsers = new ArrayList<String>();
        Collection<String> setUser = userconnections.values();
        for (String u: setUser) {
            setUsers.add(u);
        }
        return setUsers;
    }

    /**
     * 向特定的用户发送数据
     * @param conn
     * @param message
     */
    public static void sendMessageToOnlineUser(WebSocket conn, String message) {
        if (null != conn) {
            conn.send(message);
        }
    }

    /**
     * 向所有在线用户发送消息
     * @param message
     */
    public static void sendMessageToOnlineAllUser(String message) {
        Set<WebSocket> keySet = userconnections.keySet();
        synchronized (keySet) {
            for (WebSocket conn : keySet) {
                String user = userconnections.get(conn);
                if (user != null) {
                    conn.send(message);
                }
            }
        }
    }
}
