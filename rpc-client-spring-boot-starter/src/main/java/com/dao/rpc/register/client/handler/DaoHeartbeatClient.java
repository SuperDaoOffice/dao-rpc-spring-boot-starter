package com.dao.rpc.register.client.handler;

import com.dao.rpc.common.coder.DaoDecoder;
import com.dao.rpc.common.coder.DaoEncoder;
import com.dao.rpc.common.exception.DaoException;
import com.dao.rpc.common.rpc.RegisterProperties;
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

import javax.annotation.PostConstruct;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DaoHeartbeatClient {

    private RegisterProperties registerProperties;

    public DaoHeartbeatClient(RegisterProperties registerProperties) {
        this.registerProperties = registerProperties;
    }


    @PostConstruct
    public void start() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        NioEventLoopGroup group = new NioEventLoopGroup();
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
                                .addLast(new DaoConnReqHandler())
                                .addLast(new DaoConnResHandler(latch))
                                .addLast(new DaoHeartbeatResHandler())
                                .addLast(new DaoAddServerHandler())
                                .addLast(new DaoDeleteServerHandler())
                                .addLast(new DaoClientExceptionHandler());
                    }
                });
        ChannelFuture future = b.connect(registerProperties.getHost(),
                registerProperties.getPort()).sync();
        future.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("断开连接...");
                group.shutdownGracefully();
            }
        });
        if (!latch.await(10, TimeUnit.SECONDS)) {
            throw new DaoException("没有接收到注册中心响应...");
        }

    }
}
