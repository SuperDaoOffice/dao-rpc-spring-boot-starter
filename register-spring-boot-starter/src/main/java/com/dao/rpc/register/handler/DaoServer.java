package com.dao.rpc.register.handler;

import com.dao.rpc.common.coder.DaoDecoder;
import com.dao.rpc.common.coder.DaoEncoder;
import com.dao.rpc.register.handler.DaoConnResHandler;
import com.dao.rpc.register.handler.DaoHeartbeatReqHandler;
import com.dao.rpc.register.handler.DaoServerExceptionHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.TimeUnit;

public class DaoServer {

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
                                    .addLast(new DaoDecoder())
                                    .addLast(new DaoEncoder())
                                    .addLast(new DaoConnResHandler())
                                    .addLast(new DaoHeartbeatReqHandler(15000, TimeUnit.MILLISECONDS))
                                    .addLast(new DaoServerExceptionHandler());
                        }
                    });
            ChannelFuture future = b.bind(8888).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
