package com.dao.rpc.register.client.handler;

import com.dao.rpc.common.exception.InvokeExceptionEntity;
import com.dao.rpc.common.protocol.Message;
import com.dao.rpc.common.protocol.MessageType;
import com.dao.rpc.register.client.entity.DaoResponseFuture;
import com.dao.rpc.register.client.entity.RemoteServices;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

public class DaoRemoteCallExceptionHandler extends ChannelDuplexHandler {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        if (message.getHeader().getMessageType() == MessageType.EXCEPTION.getValue()) {
            InvokeExceptionEntity entity = (InvokeExceptionEntity) message.getContent();
            DaoResponseFuture future = RemoteServices.getFuture(entity.getRequestId());
            if (future != null) {
                future.setExceptionEntity(entity);
            }
        }
        ctx.fireChannelRead(msg);
    }


}
