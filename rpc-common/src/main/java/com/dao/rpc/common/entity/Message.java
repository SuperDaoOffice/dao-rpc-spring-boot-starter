package com.dao.rpc.common.entity;

import java.io.Serializable;

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
     * @param connRequest
     * @return
     */
    public static Message buildConnMessage(ConnRequest connRequest) {
        Header header = new Header();
        header.setMessageType(MessageType.CONN_REQ.getValue());
        Message<ConnRequest> message = new Message<>();
        message.setHeader(header);
        message.setContent(connRequest);
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

    public static Message<String> buildExceptionMessage(){
        Message<String> message = new Message<>();
        Header header = new Header();
        header.setMessageType(MessageType.EXCEPTION.getValue());
        message.setHeader(header);
        return message;
    }
}
