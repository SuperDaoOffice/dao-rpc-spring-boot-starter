package com.dao.rpc.register.client.handler;

import com.dao.rpc.common.coder.DaoDecoder;
import com.dao.rpc.common.coder.DaoEncoder;
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

public class DaoHeartbeatClient {

    private RegisterProperties registerProperties;

    private DaoConnReqHandler connReqHandler;

    private DaoConnResHandler connResHandler;

    private DaoHeartbeatResHandler heartbeatResHandler;

    private DaoAddServerHandler addServerHandler;

    private DaoDeleteServerHandler deleteServerHandler;

    private DaoClientExceptionHandler clientExceptionHandler;

    public DaoHeartbeatClient( DaoConnReqHandler reqHandler,
                              DaoConnResHandler resHandler, DaoHeartbeatResHandler heartbeatResHandler,
                              DaoAddServerHandler addServerHandler, DaoDeleteServerHandler deleteServerHandler,
                              DaoClientExceptionHandler exceptionHandler,
                              RegisterProperties registerProperties) {
        this.connReqHandler = reqHandler;
        this.connResHandler = resHandler;
        this.heartbeatResHandler = heartbeatResHandler;
        this.addServerHandler = addServerHandler;
        this.deleteServerHandler = deleteServerHandler;
        this.clientExceptionHandler = exceptionHandler;
        this.registerProperties = registerProperties;
    }


    @PostConstruct
    public void start() throws InterruptedException {
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
                                    .addLast(connReqHandler)
                                    .addLast(connResHandler)
                                    .addLast(heartbeatResHandler)
                                    .addLast(addServerHandler)
                                    .addLast(deleteServerHandler)
                                    .addLast(clientExceptionHandler);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
