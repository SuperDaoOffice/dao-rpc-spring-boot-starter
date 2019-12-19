package com.dao.rpc.common.protocol;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.dao.rpc.common.protocol.Constant.MAGIC;
import static com.dao.rpc.common.protocol.Constant.VERSION;

/**
 * @author HuChiHui
 * @date 2019/11/25 下午 17:30
 * @description
 */
public class Header implements Serializable {

    private byte magic = MAGIC;

    private byte version = VERSION;

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
