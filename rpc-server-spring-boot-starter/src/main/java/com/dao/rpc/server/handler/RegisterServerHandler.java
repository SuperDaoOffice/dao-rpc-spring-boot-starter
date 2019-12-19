package com.dao.rpc.server.handler;

import com.dao.rpc.common.protocol.Header;
import com.dao.rpc.common.protocol.Message;
import com.dao.rpc.common.protocol.MessageType;
import com.dao.rpc.common.rpc.RegisterServer;
import com.dao.rpc.common.rpc.RemoteAddress;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class RegisterServerHandler extends ChannelInboundHandlerAdapter {

    private RegisterServer registerServer;

    public RegisterServerHandler(List<String> exportServerList, Integer port) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            this.registerServer = new RegisterServer(new RemoteAddress(host, port), exportServerList);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Message<RegisterServer> message = new Message<>();
        Header header = new Header();
        header.setMessageType(MessageType.REGISTER_SERVER.getValue());
        message.setHeader(header);
        message.setContent(registerServer);
        ctx.writeAndFlush(message);
    }

}
