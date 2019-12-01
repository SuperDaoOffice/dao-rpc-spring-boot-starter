package com.dao.rpc.register.handler;

import com.dao.rpc.common.entity.ConnRequest;
import com.dao.rpc.common.entity.Message;
import com.dao.rpc.common.entity.MessageType;
import com.dao.rpc.common.entity.RegistedServer;
import com.dao.rpc.common.exception.DaoException;
import com.dao.rpc.common.util.JacksonUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.springframework.util.StringUtils;

public class DaoConnResHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Message message = (Message) msg;
        if (message.getHeader().getMessageType() == MessageType.CONN_REQ.getValue()) {
            System.out.println("接收到连接请求: " + JacksonUtil.writeValueAsString(message));
            Object content = message.getContent();
            validateContent(content);
            ConnRequest request = (ConnRequest) content;
            RegistedServer.addServerNode(request);
        }
        ctx.fireChannelRead(msg);
    }

    private void validateContent(Object content) {
        if (content instanceof ConnRequest) {
            ConnRequest request = (ConnRequest) content;
            if (!StringUtils.isEmpty(request.getIp()) && !StringUtils.isEmpty(request.getPort())
                    && !StringUtils.isEmpty(request.getServerName())) {
                return;
            }
        }
        throw new DaoException("请求体不正确: " + JacksonUtil.writeValueAsString(content));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
