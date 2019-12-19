package com.dao.rpc.register.handler;

import com.dao.rpc.common.protocol.Message;
import com.dao.rpc.common.protocol.MessageType;
import com.dao.rpc.common.rpc.RegisterServer;
import com.dao.rpc.common.rpc.RemoteAddress;
import com.dao.rpc.register.util.RegisterCache;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import static com.dao.rpc.register.util.RegisterCache.notifyAllClientWithAddedServer;

public class RegisterServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        if (MessageType.REGISTER_SERVER.getValue() == message.getHeader().getMessageType()) {
            RegisterServer registerServer = (RegisterServer) message.getContent();
            RemoteAddress address = registerServer.getAddress();
            for (String serverName : registerServer.getServiceNameList()) {
                RegisterCache.addServerNode(serverName, address);
                notifyAllClientWithAddedServer(serverName, address);
            }
        }
        ctx.fireChannelRead(msg);
    }
}
