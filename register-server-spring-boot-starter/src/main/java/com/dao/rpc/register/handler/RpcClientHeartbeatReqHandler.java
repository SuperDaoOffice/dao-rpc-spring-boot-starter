package com.dao.rpc.register.handler;

import com.dao.rpc.common.protocol.Message;
import com.dao.rpc.common.protocol.MessageType;
import com.dao.rpc.common.rpc.RemoteAddress;
import com.dao.rpc.register.util.RegisterCache;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class RpcClientHeartbeatReqHandler extends HeartbeatHandler {


    public RpcClientHeartbeatReqHandler(long timeout, TimeUnit unit) {
        super(timeout, unit);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        if (message.getHeader().getMessageType() == MessageType.CONN_REQ.getValue()) {
            System.out.println("连接进入...");
            ctx.pipeline().remove(RpcServerHeartbeatHandler.class);
            this.heartbeatTask = ctx.executor()
                    .scheduleAtFixedRate(new HeartbeatTask(ctx), 5000, 5000, TimeUnit.MILLISECONDS);
        } else if (message.getHeader().getMessageType() == MessageType.HEART_BEAT_RES.getValue()) {
            System.out.println("注册中心收到心跳......");
            super.channelRead(ctx, msg);
            return;
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    protected void readTimedOut(ChannelHandlerContext ctx) throws Exception {
        removeHeartbeatTask();
        //移除客户端
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        RegisterCache.removeClientChannel(new RemoteAddress(address.getHostName(), address.getPort()));
        System.out.println("客户端断开连接: host: " + address.getHostName() + "; port:" + address.getPort());
        super.readTimedOut(ctx);
    }


}
