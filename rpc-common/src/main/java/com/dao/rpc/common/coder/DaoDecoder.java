package com.dao.rpc.common.coder;

import com.dao.rpc.common.protocol.Header;
import com.dao.rpc.common.protocol.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;
import java.util.Map;

import static com.dao.rpc.common.protocol.Constant.UTF_8;


/**
 * @author HuChiHui
 * @date 2019/11/25 下午 18:10
 * @description
 */
public class DaoDecoder extends ByteToMessageDecoder {

    private ObjectDecoder objectDecoder;

    public DaoDecoder() {
        this.objectDecoder = new ObjectDecoder();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Header header = new Header();
        in.readByte();
        in.readByte();
        header.setSize(in.readInt());
        header.setMessageType(in.readByte());
        int otherHeadLength = in.readInt();
        if (otherHeadLength > 0) {
            Map<String, Object> otherHeaderMap = header.getOtherHeaderMap();
            for (int i = 0; i < otherHeadLength; i++) {
                int keySize = in.readInt();
                byte[] keyBytes = new byte[keySize];
                in.readBytes(keyBytes);
                String keyStr = new String(keyBytes, UTF_8);
                Object value = objectDecoder.decode(in);
                otherHeaderMap.put(keyStr, value);
            }
        }
        Message<Object> message = new Message<>();
        message.setHeader(header);
        Object body = objectDecoder.decode(in);
        message.setContent(body);
        out.add(message);

    }
}
