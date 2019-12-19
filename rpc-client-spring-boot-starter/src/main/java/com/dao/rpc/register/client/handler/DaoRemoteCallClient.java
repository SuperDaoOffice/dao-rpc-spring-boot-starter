package com.dao.rpc.register.client.handler;

import com.dao.rpc.common.coder.DaoDecoder;
import com.dao.rpc.common.coder.DaoEncoder;
import com.dao.rpc.common.protocol.Message;
import com.dao.rpc.common.rpc.RemoteAddress;
import com.dao.rpc.common.rpc.Request;
import com.dao.rpc.register.client.entity.DaoResponseFuture;
import com.dao.rpc.register.client.entity.RemoteServices;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author HuChiHui
 * @date 2019/11/09 下午 13:56
 * @description
 */
public class DaoRemoteCallClient {

    private ChannelFuture channelFuture;

    private RemoteAddress remoteAddress;

    public DaoRemoteCallClient(RemoteAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
        start();
    }

    public void start() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 2,
                                    4, 0, 0))
                                    .addLast(new DaoDecoder())
                                    .addLast(new DaoEncoder())
                                    .addLast(new DaoResponseHandler())
                                    .addLast(new DaoRemoteCallExceptionHandler());
                        }
                    });
            ChannelFuture future = b.connect(remoteAddress.getIp(), remoteAddress.getPort()).sync();
            this.channelFuture = future;
            future.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("断开连接...");
                    group.shutdownGracefully();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object remoteCall(Message<Request> message) throws InterruptedException {
        DaoResponseFuture<Object> responseFuture = new DaoResponseFuture<>();
        RemoteServices.addFutureCache(message.getContent().getRequestId(), responseFuture);
        channelFuture.channel().writeAndFlush(message);
        return responseFuture.get(8, TimeUnit.SECONDS);
    }
}
