package com.dao.rpc.server.handler;

import com.dao.rpc.common.exception.ExceptionCode;
import com.dao.rpc.common.exception.InvokeExceptionEntity;
import com.dao.rpc.common.protocol.Header;
import com.dao.rpc.common.protocol.Message;
import com.dao.rpc.common.protocol.MessageType;
import com.dao.rpc.common.rpc.Request;
import com.dao.rpc.common.rpc.Response;
import com.dao.rpc.server.config.ActionMethod;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import static com.dao.rpc.server.config.InitService.retrieveMethod;

public class InvokeMethodHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        Header header = message.getHeader();
        if (header == null || header.getMessageType() != MessageType.REQUEST.getValue()) {
            ctx.fireChannelRead(msg);
            return;
        }
        Request request = (Request) message.getContent();
        String url = request.retrieveCompletedMethodUrl();
        ActionMethod actionMethod = retrieveMethod(url);
        if (actionMethod == null) {
            ctx.writeAndFlush(buildInvokeMessage(request.getRequestId(), "远程服务没有此方法: " + url));
            return;
        }
        try {
            Object result = actionMethod.call(request.getArgs());
            Response<Object> response = new Response<>(request.getRequestId(), result);
            Message<Object> successMessage = Message.buildInvokeSuccessMessage(response);
            ctx.writeAndFlush(successMessage);
        } catch (Exception e) {
            e.printStackTrace();
            ctx.writeAndFlush(buildInvokeMessage(request.getRequestId(), e.getMessage()));
        }

    }

    private Message<InvokeExceptionEntity> buildInvokeMessage(String requestId, String msg) {
        Message<InvokeExceptionEntity> message = new Message<>();
        Header header = new Header();
        header.setMessageType(MessageType.EXCEPTION.getValue());
        message.setHeader(header);
        InvokeExceptionEntity exceptionEntity = new InvokeExceptionEntity();
        exceptionEntity.setRequestId(requestId);
        exceptionEntity.setCode(ExceptionCode.INVOKE_ERROR.getCode());
        exceptionEntity.setMsg(msg);
        message.setContent(exceptionEntity);
        return message;
    }
}
