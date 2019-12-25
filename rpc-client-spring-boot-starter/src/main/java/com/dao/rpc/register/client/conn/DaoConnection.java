package com.dao.rpc.register.client.conn;

import com.dao.rpc.common.coder.DaoDecoder;
import com.dao.rpc.common.coder.DaoEncoder;
import com.dao.rpc.common.protocol.Message;
import com.dao.rpc.common.rpc.RemoteAddress;
import com.dao.rpc.common.rpc.Request;
import com.dao.rpc.register.client.config.DaoConnectionProperties;
import com.dao.rpc.register.client.entity.DaoResponseFuture;
import com.dao.rpc.register.client.entity.RemoteServices;
import com.dao.rpc.register.client.handler.DaoRemoteCallExceptionHandler;
import com.dao.rpc.register.client.handler.DaoResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author HuChiHui
 * @date 2019/12/24 上午 10:32
 * @description
 */
public class DaoConnection implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(DaoConnection.class);

    private Channel channel;

    private Integer connTimeout;

    private Integer invokeTimeout;

    private Integer reconnectCount;

    private CountDownLatch latch = new CountDownLatch(1);

    private long expireTime;

    private ArrayBlockingQueue<DaoConnection> queue;


    private DaoConnection(DaoConnectionProperties connectionProperties, long expireTime, ArrayBlockingQueue<DaoConnection> queue) {
        this.connTimeout = Optional.ofNullable(connectionProperties).map(DaoConnectionProperties::getConnTimeout).orElse(10000);
        this.invokeTimeout = Optional.ofNullable(connectionProperties).map(DaoConnectionProperties::getInvokeTimeout).orElse(10000);
        this.reconnectCount = Optional.ofNullable(connectionProperties).map(DaoConnectionProperties::getReconnectCount).orElse(3);
        this.expireTime = expireTime;
        this.queue = queue;
    }

    static DaoConnection createConnection(DaoConnectionProperties connectionProperties, long expireTime,
                                          ArrayBlockingQueue<DaoConnection> queue) {
        return new DaoConnection(connectionProperties, expireTime, queue);
    }


    void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * 获取连接过期时间
     *
     * @return
     */
    long getExpireTime() {
        return expireTime;
    }

    /**
     * 连接远程
     *
     * @param remoteAddress
     * @return
     * @throws InterruptedException
     */
    boolean connect(RemoteAddress remoteAddress) throws InterruptedException {
        int reconnectCont = reconnectCount;
        startConnect(remoteAddress);
        if (latch.await((long) connTimeout * reconnectCont, TimeUnit.MILLISECONDS)) {
            return true;
        }
        return false;
    }

    /**
     * 重连
     */
    private void startConnect(RemoteAddress remoteAddress) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connTimeout)
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
        ChannelFuture future = b.connect(remoteAddress.getIp(), remoteAddress.getPort())
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (!future.isSuccess()) {
                            log.error("连接失败", future.cause());
                            if (reconnectCount-- > 0) {
                                startConnect(remoteAddress);
                            }
                        } else {
                            channel = future.channel();
                            latch.countDown();
                        }
                    }
                });
        future.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.info("断开连接");
                group.shutdownGracefully();
            }
        });

    }

    /**
     * 远程调用服务
     *
     * @param message
     * @return
     * @throws InterruptedException
     */
    public Object remoteInvoke(Message<Request> message) throws InterruptedException {
        DaoResponseFuture<Object> responseFuture = new DaoResponseFuture<>();
        RemoteServices.addFutureCache(message.getContent().getRequestId(), responseFuture);
        channel.writeAndFlush(message);
        Object result = responseFuture.get(invokeTimeout, TimeUnit.MILLISECONDS);
        release();
        return result;
    }

    @Override
    public void close() {
        if (channel != null) {
            channel.close();
        }
    }

    /**
     * 释放连接回队列
     */
    public void release() {
        System.out.println(Thread.currentThread().getName() + "归还连接");
        queue.add(this);
    }

}
