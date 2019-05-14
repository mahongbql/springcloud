package com.example.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

import java.net.InetSocketAddress;

/**
 * 绑定服务器到监听的端口，配置Channel，将入站消息通知给EchoServerHandler实例
 * @version 1.0.0
 * @since 2017/5/25上午12:55
 */
public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        System.err.println("Usage: " + EchoServer.class.getSimpleName() + "<port>");
        int port = 8999;
        // 设置端口
        new EchoServer(port).start();
    }

    public void start() throws Exception{
        // 创建EventLoopGroup
        final EchoServerHandler serverHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建ServerBootstrap
            ServerBootstrap b = new ServerBootstrap();
            /*
             * 指定所使用的NIO传输Channel、
             * 使用指定的端口设置套接字地址、
             * 添加EchoHandler到子Channel的ChannelPipeline
             */
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(32));
                            socketChannel.pipeline().addLast(serverHandler);
                        }
                    });
            ChannelFuture future = b.bind().sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
