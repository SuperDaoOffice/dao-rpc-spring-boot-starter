package com.dao.rpc.register.client.handler;

import com.dao.rpc.common.entity.Message;
import com.dao.rpc.common.entity.MessageType;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

public class DaoClientExceptionHandler extends ChannelDuplexHandler {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        if (message.getHeader().getMessageType() == MessageType.EXCEPTION.getValue()) {
            System.out.println("服务端发生异常，exception: " + message.getContent());
            ctx.close();
        } else {
            ctx.fireChannelRead(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}
