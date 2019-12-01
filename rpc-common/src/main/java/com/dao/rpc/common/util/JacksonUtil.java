package com.dao.rpc.common.util;

import com.dao.rpc.common.exception.JsonOperationException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JacksonUtil {

    private static ObjectMapper objectMapper = null;

    static {
        objectMapper = new ObjectMapper();
        // 对象字段全部列入
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 设置时区
        objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        // 取消默认转换timestamps形式
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // 忽略空bean转json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        // 统一日期格式yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        //设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    /**
     * java对象转为字符串，null对象即为"null"
     *
     * @param obj
     * @return
     * @throws JsonProcessingException
     */
    public static String writeValueAsString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new JsonOperationException("JSON 转化异常！");
        }
    }

    /**
     * java对象转string ，null对象转为"" 空字符串
     *
     * @param obj
     * @return
     */
    public static String writeValueAsStringForNull(Object obj) {
        objectMapper.getSerializerProvider().setNullValueSerializer(new
                                                                            JsonSerializer<Object>() {
                                                                                @Override
                                                                                public void serialize(Object arg0, JsonGenerator arg1, SerializerProvider
                                                                                        arg2)
                                                                                        throws IOException, JsonProcessingException {
                                                                                    arg1.writeString("");
                                                                                }
                                                                            });
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new JsonOperationException("JSON 转化异常！");
        }
    }

    /**
     * 将对象转换为JSON字符串二进制数组
     *
     * @param obj
     * @return byte[]
     * @throws
     * @Title: toJsonByte
     */
    public static byte[] toJsonByte(Object obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (Exception e) {
            throw new JsonOperationException("将对象转换为JSON字符串二进制数组错误！！");
        }
    }

    /**
     * 将JSON字符串转为Map
     *
     * @param json
     * @return
     */
    public static Map<String, Object> toMap(String json) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> maps = objectMapper.readValue(json, Map.class);//转成map
            return maps;
        } catch (Exception e) {
            throw new JsonOperationException("字符串转为map异常！！");
        }
    }

    /**
     * 将对象转换为JSON字符串流
     *
     * @param obj
     * @return OutputStream
     * @throws
     * @Title: toJsonOutStream
     */
    public static OutputStream toJsonOutStream(Object obj) {
        try {
            OutputStream os = new ByteArrayOutputStream();
            objectMapper.writeValue(os, obj);
            return os;
        } catch (Exception e) {
            throw new JsonOperationException("无法转化为字符串流！！");
        }
    }

    /**
     * 将JSON字符串转换为对象
     *
     * @param clazz
     * @param json
     * @return T
     * @throws
     * @Title: toObject
     */
    public static <T> T toObject(Class<T> clazz, String json) {
        T obj = null;
        try {
            if (StringUtils.isEmpty(json)) {
                return null;
            }
            obj = (T) objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new JsonOperationException("json字符串转化错误！！");
        }
        return obj;
    }

    /**
     * 将JSON二进制数组转换为对象
     *
     * @param clazz
     * @param bytes
     * @return T
     * @throws
     * @Title: toObject
     */
    public static <T> T toObject(Class<T> clazz, byte[] bytes) {
        T obj = null;
        try {
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            obj = (T) objectMapper.readValue(bytes, clazz);
        } catch (Exception e) {
            throw new JsonOperationException("二进制转化错误！！");
        }
        return obj;
    }

    /**
     * 将Json字符串转换成List
     *
     * @param clazz
     * @param json
     * @return
     */
    public static <T> List<T> toObjectList(Class<T> clazz, String json) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, clazz);
            return objectMapper.readValue(json,javaType);
        } catch (IOException e) {
            e.printStackTrace();
            throw new JsonOperationException("json字符串转为list异常！！");
        }
    }

    /**
     * 转换成json树
     *
     * @param pageMap
     * @param count
     * @return String
     * @throws
     * @Title: toJsonTree
     */
    public static String toJsonTree(List<ConcurrentHashMap<String, Object>> pageMap, Object... count) {
        List<ConcurrentHashMap<String, Object>> myMap = new ArrayList<ConcurrentHashMap<String, Object>>();

        // 添加冗余父节点字段
        for (ConcurrentHashMap<String, Object> map : pageMap) {
            // 迭代字段查询key为parentid 的key
            ConcurrentHashMap<String, Object> tempMap = new ConcurrentHashMap<String, Object>();
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                Object value = map.get(key);
                if (key.toLowerCase().equals("parentid")) {
                    tempMap.put("_parentId", value);
                }
                tempMap.put(key.toLowerCase(), value);
            }
            myMap.add(tempMap);
        }

        HashMap<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("total", count);
        jsonMap.put("rows", myMap);
        try {
            return objectMapper.writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            throw new JsonOperationException("转换json树异常！！");
        }
    }
}
