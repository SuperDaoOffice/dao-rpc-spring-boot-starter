package com.dao.rpc.common.util;

import com.caucho.hessian.io.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author HuChiHui
 * @date 2019/11/25 下午 17:53
 * @description
 */
public class HessianUtil {

    public static byte[] serialize(Object obj) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        AbstractHessianOutput out = new Hessian2Output(os);

        out.setSerializerFactory(new SerializerFactory());
        try {
            out.writeObject(obj);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                out.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return os.toByteArray();
    }

    public static <T> T deserialize(byte[] bytes) {
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        AbstractHessianInput in = new Hessian2Input(is);

        in.setSerializerFactory(new SerializerFactory());
        T value;
        try {
            value = (T) in.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                in.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return value;
    }
}
