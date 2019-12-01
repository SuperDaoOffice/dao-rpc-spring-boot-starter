package com.dao.rpc.register.handler;

import com.dao.rpc.common.entity.Message;
import com.dao.rpc.common.entity.MessageType;
import com.dao.rpc.common.entity.RegistedServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.ScheduledFuture;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class DaoHeartbeatReqHandler extends ReadTimeoutHandler {

    private volatile ScheduledFuture<?> heartbeat;

    public DaoHeartbeatReqHandler(long timeout, TimeUnit unit) {
        super(timeout, unit);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        if (message.getHeader().getMessageType() == MessageType.CONN_REQ.getValue()) {
            this.heartbeat = ctx.executor()
                    .scheduleAtFixedRate(new HeartbeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);
        } else if (message.getHeader().getMessageType() == MessageType.HEART_BEAT_RES.getValue()) {
            System.out.println("服务端收到客户端的心跳......");
        }
        super.channelRead(ctx, msg);
    }

    @Override
    protected void readTimedOut(ChannelHandlerContext ctx) throws Exception {
        removeHeartbeatTask();
        //移除服务
        InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
        RegistedServer.removeServerNode(address.getHostName(), address.getPort());
        System.out.println("移除服务......");
        super.readTimedOut(ctx);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        removeHeartbeatTask();
        ctx.fireExceptionCaught(cause);
    }

    private class HeartbeatTask implements Runnable {
        private final ChannelHandlerContext ctx;

        public HeartbeatTask(final ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            Message heatBeat = Message.buildHeartbeatReq();
            System.out.println("服务端发送心跳给客户端");
            ctx.writeAndFlush(heatBeat);
        }

    }

    /**
     * 移除心跳任务
     */
    private void removeHeartbeatTask() {
        if (heartbeat != null) {
            heartbeat.cancel(true);
            heartbeat = null;
        }
    }
}
