package com.dao.rpc.register.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

public class HeartbeatHandler extends ReadTimeoutHandler {

    protected volatile ScheduledFuture<?> heartbeatTask;

    public HeartbeatHandler(long timeout, TimeUnit unit) {
        super(timeout, unit);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        removeHeartbeatTask();
        ctx.fireExceptionCaught(cause);
    }


    /**
     * 移除心跳任务
     */
    protected void removeHeartbeatTask() {
        if (heartbeatTask != null) {
            heartbeatTask.cancel(true);
            heartbeatTask = null;
        }
    }
}
