package com.dao.rpc.server.handler;

import com.dao.rpc.common.exception.ExceptionCode;
import com.dao.rpc.common.exception.ExceptionEntity;
import com.dao.rpc.common.protocol.Header;
import com.dao.rpc.common.protocol.Message;
import com.dao.rpc.common.protocol.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerExceptionHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        Message<ExceptionEntity> message = buildExceptionMessage(cause.getMessage());
        ctx.writeAndFlush(message);
        ctx.close();
    }

    private Message<ExceptionEntity> buildExceptionMessage(String msg) {
        ExceptionEntity exceptionEntity = new ExceptionEntity();
        exceptionEntity.setCode(ExceptionCode.UNKNOWN_ERROR.getCode());
        exceptionEntity.setMsg(msg);
        Header header = new Header();
        header.setMessageType(MessageType.EXCEPTION.getValue());
        Message<ExceptionEntity> message = new Message<>();
        message.setHeader(header);
        message.setContent(exceptionEntity);
        return message;
    }
}
