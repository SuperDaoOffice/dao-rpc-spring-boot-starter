package com.dao.rpc.register.handler;

import com.dao.rpc.common.exception.ExceptionEntity;
import com.dao.rpc.common.protocol.Header;
import com.dao.rpc.common.protocol.Message;
import com.dao.rpc.common.protocol.MessageType;
import com.dao.rpc.register.exception.RegisterServerException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import static com.dao.rpc.common.exception.ExceptionCode.UNKNOWN_ERROR;


@ChannelHandler.Sharable
public class RegisterExceptionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        Message<ExceptionEntity> exceptionMessage;
        if (cause instanceof RegisterServerException) {
            RegisterServerException daoException = (RegisterServerException) cause;
            exceptionMessage = buildExceptionMessage(new ExceptionEntity(daoException.getCode(), daoException.getMessage()));
        } else {
            exceptionMessage = buildExceptionMessage(new ExceptionEntity(UNKNOWN_ERROR.getCode(), cause.getMessage()));
        }
        ctx.writeAndFlush(exceptionMessage);
        ctx.close();
    }

    private Message<ExceptionEntity> buildExceptionMessage(ExceptionEntity entity) {
        Message<ExceptionEntity> message = new Message<>();
        Header header = new Header();
        header.setMessageType(MessageType.EXCEPTION.getValue());
        message.setHeader(header);
        message.setContent(entity);
        return message;
    }
}
