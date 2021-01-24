package com.nettychat.server.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * JSON 工具类
 */
public class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * object to json
     *
     * @param value 待转换 java 对象
     * @return java 对象对应的 json 字符串
     */
    public static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json to object
     *
     * @param content   待转换 json 字符转
     * @param valueType 待转换为的对象的类型
     * @return json 字符串对应的 java 对象
     */
    public static <T> T toObj(String content, Class<T> valueType) {
        try {
            return MAPPER.readValue(content, valueType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json to object list
     *
     * @param content          待转换 json 字符转
     * @param parameterClasses 待转换为的 list 中的对象类型
     * @return json 字符串对应的对象 list
     */
    public static <T> List<T> toList(String content, Class<T> parameterClasses) {
        JavaType valueType = MAPPER.getTypeFactory().constructParametricType(List.class, parameterClasses);
        try {
            return MAPPER.readValue(content, valueType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
