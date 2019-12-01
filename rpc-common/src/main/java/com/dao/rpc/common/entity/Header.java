package com.dao.rpc.common.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HuChiHui
 * @date 2019/11/25 下午 17:30
 * @description
 */
public class Header implements Serializable {

    private byte magic = (byte) 0xFF;

    private byte version = (byte) 0x01;

    private byte messageType;

    private int size;

    private Map<String, Object> otherHeaderMap = new HashMap<>(16);

    public byte getMagic() {
        return magic;
    }

    public byte getVersion() {
        return version;
    }

    public byte getMessageType() {
        return messageType;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public Map<String, Object> getOtherHeaderMap() {
        return otherHeaderMap;
    }

    @Override
    public String toString() {
        return "Header{" +
                "messageType=" + messageType +
                ", size=" + size +
                ", otherHeaderMap=" + otherHeaderMap +
                '}';
    }
}
