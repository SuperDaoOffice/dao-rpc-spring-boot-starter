package com.dao.rpc.register.client.handler;

import com.dao.rpc.common.protocol.Header;
import com.dao.rpc.common.protocol.Message;
import com.dao.rpc.common.protocol.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DaoConnReqHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Header header = new Header();
        header.setMessageType(MessageType.CONN_REQ.getValue());
        Message<Object> message = new Message<>();
        message.setHeader(header);
        ctx.writeAndFlush(message);
    }

}
