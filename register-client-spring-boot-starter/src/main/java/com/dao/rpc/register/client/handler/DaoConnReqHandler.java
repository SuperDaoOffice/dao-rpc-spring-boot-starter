package com.dao.rpc.register.client.handler;

import com.dao.rpc.common.entity.ConnRequest;
import com.dao.rpc.common.entity.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DaoConnReqHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ConnRequest request = new ConnRequest();
        request.setIp("localhost");
        request.setPort(9000);
        request.setServerName("demo1");
        Message message = Message.buildConnMessage(request);
        ctx.writeAndFlush(message);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}
