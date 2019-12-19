package com.dao.rpc.register.handler;

import com.dao.rpc.common.protocol.Message;
import com.dao.rpc.common.protocol.MessageType;
import com.dao.rpc.common.rpc.ConnRequest;
import com.dao.rpc.common.rpc.RemoteAddress;
import com.dao.rpc.common.util.JacksonUtil;
import com.dao.rpc.register.exception.RegisterServerException;
import com.dao.rpc.register.util.RegisterCache;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.dao.rpc.common.exception.ExceptionCode.CONN_ERROR;
import static com.dao.rpc.register.util.RegisterCache.*;

@ChannelHandler.Sharable
public class ConnResHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        if (message.getHeader().getMessageType() == MessageType.CONN_REQ.getValue()) {
            System.out.println("接收到连接请求: " + JacksonUtil.writeValueAsString(message));
            ConcurrentHashMap<String, List<RemoteAddress>> map = RegisterCache.getRegisteredServerMap();
            Message response = Message.buildConnSuccessMessage(map);
            ctx.writeAndFlush(response);

            InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
            addClientChannel(
                    new RemoteAddress(address.getHostName(), address.getPort()), ctx.channel());
        }
        ctx.fireChannelRead(msg);
    }



}
