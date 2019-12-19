package com.dao.rpc.register.client.handler;

import com.dao.rpc.common.exception.ExceptionEntity;
import com.dao.rpc.common.protocol.Message;
import com.dao.rpc.common.protocol.MessageType;
import com.dao.rpc.register.client.exception.DaoRegisterClientException;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

public class DaoClientExceptionHandler extends ChannelDuplexHandler {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        if (message.getHeader().getMessageType() == MessageType.EXCEPTION.getValue()) {
            ExceptionEntity exceptionEntity = (ExceptionEntity) message.getContent();
            throw new DaoRegisterClientException("服务端发生异常：code: " + exceptionEntity.getCode()
                    + "; msg: " + exceptionEntity.getMsg());
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}
