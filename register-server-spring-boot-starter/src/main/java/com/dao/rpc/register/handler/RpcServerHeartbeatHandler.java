package com.dao.rpc.register.handler;

import com.dao.rpc.common.protocol.Message;
import com.dao.rpc.common.protocol.MessageType;
import com.dao.rpc.common.rpc.RemoteAddress;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static com.dao.rpc.register.util.RegisterCache.notifyAllClientWithDeletedServer;
import static com.dao.rpc.register.util.RegisterCache.removeServerNode;

public class RpcServerHeartbeatHandler extends HeartbeatHandler {

    public RpcServerHeartbeatHandler(long timeout, TimeUnit unit) {
        super(timeout, unit);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        if (message.getHeader().getMessageType() == MessageType.REGISTER_SERVER.getValue()) {
            ctx.pipeline().remove(RpcClientHeartbeatReqHandler.class);
            this.heartbeatTask = ctx.executor()
                    .scheduleAtFixedRate(new HeartbeatTask(ctx), 5000, 5000, TimeUnit.MILLISECONDS);
        } else if (message.getHeader().getMessageType() == MessageType.HEART_BEAT_RES.getValue()) {
            System.out.println("注册中心收到心跳......");
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    protected void readTimedOut(ChannelHandlerContext ctx) throws Exception {
        removeHeartbeatTask();
        //移除服务
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        notifyAllClientWithDeletedServer(new RemoteAddress(address.getHostName(), address.getPort()));
        removeServerNode(new RemoteAddress(address.getHostName(), address.getPort()));
        System.out.println("移除服务......");
        super.readTimedOut(ctx);
    }

}
