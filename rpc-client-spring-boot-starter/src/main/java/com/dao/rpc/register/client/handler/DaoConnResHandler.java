package com.dao.rpc.register.client.handler;

import com.dao.rpc.common.protocol.Message;
import com.dao.rpc.common.protocol.MessageType;
import com.dao.rpc.common.rpc.RemoteAddress;
import com.dao.rpc.register.client.entity.RemoteServices;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class DaoConnResHandler extends ChannelInboundHandlerAdapter {


    private CountDownLatch latch;

    public DaoConnResHandler(CountDownLatch latch) {
        this.latch = latch;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        if (message.getHeader().getMessageType() == MessageType.CONN_RES.getValue()) {
            latch.countDown();
            ConcurrentHashMap<String, List<RemoteAddress>> map =
                    (ConcurrentHashMap<String, List<RemoteAddress>>) message.getContent();
            for (Map.Entry<String, List<RemoteAddress>> entry : map.entrySet()) {
                RemoteServices.addRemoteAddressMap(entry.getKey(), entry.getValue());
            }
        }
        ctx.fireChannelRead(msg);
    }

}
