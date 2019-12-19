package com.dao.rpc.register.client.handler;

import com.dao.rpc.common.protocol.Message;
import com.dao.rpc.common.protocol.MessageType;
import com.dao.rpc.common.rpc.Response;
import com.dao.rpc.register.client.entity.DaoResponseFuture;
import com.dao.rpc.register.client.entity.RemoteServices;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DaoResponseHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        if (MessageType.RESPONSE.getValue() == message.getHeader().getMessageType()) {
            Response response = (Response) message.getContent();
            DaoResponseFuture future = RemoteServices.getFuture(response.getRequestId());
            if (future != null) {
                future.setResponse(response.getResponse());
            }
        }
        ctx.fireChannelRead(msg);
    }
}
