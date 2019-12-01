package com.dao.rpc.common.coder;

import com.dao.rpc.common.util.HessianUtil;
import io.netty.buffer.ByteBuf;

/**
 * @author HuChiHui
 * @date 2019/11/09 下午 15:57
 * @description
 */
public class ObjectEncoder {

    private byte[] LENGTH_PLACE = new byte[4];

    public void encode(Object value, ByteBuf sendBuf) {
        int writerIndex = sendBuf.writerIndex();
        sendBuf.writeBytes(LENGTH_PLACE);
        sendBuf.writeBytes(HessianUtil.serialize(value));
        sendBuf.setInt(writerIndex, sendBuf.writerIndex() - writerIndex - 4);
    }
}
