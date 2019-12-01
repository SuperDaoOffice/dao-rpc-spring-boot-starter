package com.dao.rpc.register.handler;

import com.dao.rpc.common.entity.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DaoServerExceptionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        Message<String> exceptionMessage = Message.buildExceptionMessage();
        exceptionMessage.setContent(cause.getMessage());
        ctx.writeAndFlush(exceptionMessage);
        ctx.close();
    }
}
