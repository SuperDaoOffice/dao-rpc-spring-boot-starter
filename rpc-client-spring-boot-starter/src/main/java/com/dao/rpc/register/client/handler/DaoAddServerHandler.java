package com.dao.rpc.register.client.handler;

import com.dao.rpc.common.protocol.Message;
import com.dao.rpc.common.protocol.MessageType;
import com.dao.rpc.common.rpc.ServerAddress;
import com.dao.rpc.register.client.entity.RemoteServices;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DaoAddServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        if (message.getHeader().getMessageType() == MessageType.ADD_SERVER.getValue()) {
            ServerAddress address = (ServerAddress) message.getContent();
            RemoteServices.addRemoteAddressMap(address.getServerName(), address.getAddress());
        }
        ctx.fireChannelRead(msg);
    }
}
