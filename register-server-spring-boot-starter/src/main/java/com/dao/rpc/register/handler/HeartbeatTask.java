package com.dao.rpc.register.handler;

import com.dao.rpc.common.protocol.Message;
import io.netty.channel.ChannelHandlerContext;

public class HeartbeatTask implements Runnable {

    private final ChannelHandlerContext ctx;

    public HeartbeatTask(final ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void run() {
        Message heatBeat = Message.buildHeartbeatReq();
        System.out.println("注册中心发送心跳...");
        ctx.writeAndFlush(heatBeat);
    }
}
