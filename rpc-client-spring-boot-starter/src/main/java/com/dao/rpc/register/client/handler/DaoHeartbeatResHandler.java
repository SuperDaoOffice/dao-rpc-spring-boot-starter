package com.dao.rpc.register.client.handler;

import com.dao.rpc.common.protocol.Message;
import com.dao.rpc.common.protocol.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DaoHeartbeatResHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        if (message.getHeader().getMessageType() == MessageType.HEART_BEAT_REQ.getValue()) {
            System.out.println("接收到注册中心的心跳，响应其请求......");
            Message heartbeatRes = Message.buildHeartbeatRes();
            ctx.writeAndFlush(heartbeatRes);
        }
        ctx.fireChannelRead(msg);
    }
}
