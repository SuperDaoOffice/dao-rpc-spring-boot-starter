package com.dao.rpc.register.client.handler;

import com.dao.rpc.common.coder.DaoDecoder;
import com.dao.rpc.common.coder.DaoEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author HuChiHui
 * @date 2019/11/09 下午 13:56
 * @description
 */
public class DaoClient {

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
                                    .addLast(new DaoConnReqHandler())
                                    .addLast(new DaoHeartbeatResHandler())
                                    .addLast(new DaoClientExceptionHandler());
                        }
                    });
            ChannelFuture future = b.connect("localhost", 8888).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }


}
