package com.dao.rpc.register.handler;

import com.dao.rpc.common.coder.DaoDecoder;
import com.dao.rpc.common.coder.DaoEncoder;
import com.dao.rpc.register.config.ServerProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

public class RegisterServer {

    private DaoDecoder daoDecoder;

    private DaoEncoder daoEncoder;

    private ConnResHandler daoConnResHandler;

    private RegisterExceptionHandler exceptionHandler;

    private ServerProperties serverProperties;

    public RegisterServer(DaoEncoder daoEncoder, DaoDecoder daoDecoder, ServerProperties serverProperties,
                          ConnResHandler daoConnResHandler, RegisterExceptionHandler exceptionHandler) {
        this.daoDecoder = daoDecoder;
        this.daoEncoder = daoEncoder;
        this.daoConnResHandler = daoConnResHandler;
        this.exceptionHandler = exceptionHandler;
        this.serverProperties = serverProperties;
    }

    @PostConstruct
    public void start() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 2,
                                    4, 0, 0))
                                    .addLast(daoDecoder)
                                    .addLast(daoEncoder)
                                    .addLast(daoConnResHandler)
                                    .addLast(new ClientHeartbeatReqHandler(serverProperties.getTimeout(), TimeUnit.MILLISECONDS))
                                    .addLast(new ServerHeartbeatHandler(serverProperties.getTimeout(), TimeUnit.MILLISECONDS))
                                    .addLast(exceptionHandler);
                        }
                    });
            ChannelFuture future = b.bind(serverProperties.getPort()).sync();
            future.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("连接关闭...");
                    bossGroup.shutdownGracefully();
                    workGroup.shutdownGracefully();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
