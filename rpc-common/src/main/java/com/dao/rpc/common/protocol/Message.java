package com.dao.rpc.common.protocol;

import com.dao.rpc.common.rpc.ConnRequest;
import com.dao.rpc.common.rpc.RemoteAddress;
import com.dao.rpc.common.rpc.ServerAddress;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author HuChiHui
 * @date 2019/11/25 下午 17:30
 * @description
 */
public class Message<T> implements Serializable {

    private Header header;

    private T content;


    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "header=" + header +
                ", content=" + content +
                '}';
    }

    /**
     * 通知删除的服务的Message
     *
     * @param serverAddress
     * @return
     */
    public static Message<ServerAddress> buildDeletedServerMessage(ServerAddress serverAddress) {
        Header header = new Header();
        header.setMessageType(MessageType.DELETE_SERVER.getValue());
        Message<ServerAddress> message = new Message<>();
        message.setHeader(header);
        message.setContent(serverAddress);
        return message;

    }

    /**
     * 通知添加的服务的Message
     *
     * @param serverAddress
     * @return
     */
    public static Message<ServerAddress> buildAddedServerMessage(ServerAddress serverAddress) {
        Header header = new Header();
        header.setMessageType(MessageType.ADD_SERVER.getValue());
        Message<ServerAddress> message = new Message<>();
        message.setHeader(header);
        message.setContent(serverAddress);
        return message;
    }


    /**
     *
     * @return
     */
    public static Message buildConnMessage() {
        Header header = new Header();
        header.setMessageType(MessageType.CONN_REQ.getValue());
        Message<ConnRequest> message = new Message<>();
        message.setHeader(header);
        return message;
    }

    public static Message buildConnSuccessMessage(ConcurrentHashMap<String, List<RemoteAddress>> map) {
        Header header = new Header();
        header.setMessageType(MessageType.CONN_RES.getValue());
        Message<ConcurrentHashMap<String, List<RemoteAddress>>> message = new Message<>();
        message.setHeader(header);
        message.setContent(map);
        return message;
    }

    public static Message buildHeartbeatReq() {
        Message message = new Message<>();
        Header header = new Header();
        header.setMessageType(MessageType.HEART_BEAT_REQ.getValue());
        message.setHeader(header);
        return message;
    }

    public static Message buildHeartbeatRes() {
        Message message = new Message<>();
        Header header = new Header();
        header.setMessageType(MessageType.HEART_BEAT_RES.getValue());
        message.setHeader(header);
        return message;
    }


    public static Message<Object> buildInvokeSuccessMessage(Object result) {
        Message<Object> message = new Message<>();
        Header header = new Header();
        header.setMessageType(MessageType.RESPONSE.getValue());
        message.setHeader(header);
        message.setContent(result);
        return message;
    }
}
