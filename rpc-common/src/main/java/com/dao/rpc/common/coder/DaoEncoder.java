package com.dao.rpc.common.coder;

import com.dao.rpc.common.protocol.Header;
import com.dao.rpc.common.protocol.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.dao.rpc.common.protocol.Constant.UTF_8;


/**
 * @author HuChiHui
 * @date 2019/11/25 下午 17:29
 * @description
 */
public class DaoEncoder extends MessageToByteEncoder<Message> {

    private static final Logger log = LoggerFactory.getLogger(DaoEncoder.class);

    private ObjectEncoder objectEncoder;

    public DaoEncoder() {
        this.objectEncoder = new ObjectEncoder();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        Header header = msg.getHeader();
        out.writeByte(header.getMagic());
        out.writeByte(header.getVersion());
        out.writeInt(header.getSize());
        out.writeByte(header.getMessageType());

        //序列化otherHeaderMap
        Map<String, Object> otherHeaderMap = header.getOtherHeaderMap();
        if (otherHeaderMap.isEmpty()) {
            out.writeInt(0);
        } else {
            out.writeInt(otherHeaderMap.size());
            for (Map.Entry<String, Object> entry : otherHeaderMap.entrySet()) {
                byte[] keyBytes = entry.getKey().getBytes(UTF_8);
                out.writeInt(keyBytes.length);
                out.writeBytes(keyBytes);
                Object value = entry.getValue();
                objectEncoder.encode(value, out);
            }
        }

        //序列化body
        Object content = msg.getContent();
        if (content == null) {
            out.writeInt(0);
        } else {
            objectEncoder.encode(content, out);
        }
        out.setInt(2, out.readableBytes() - 6);
    }
}
