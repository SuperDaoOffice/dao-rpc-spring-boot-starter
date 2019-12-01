package com.dao.rpc.common.coder;

import com.dao.rpc.common.util.HessianUtil;
import io.netty.buffer.ByteBuf;

/**
 * @author HuChiHui
 * @date 2019/11/09 下午 16:27
 * @description
 */
public class ObjectDecoder {

    public Object decode(ByteBuf buf) {
        int length = buf.readInt();
        if (length > 0) {
            byte[] bytes = new byte[length];
            buf.readBytes(bytes);
            return HessianUtil.deserialize(bytes);
        } else {
            return null;
        }

    }
}
