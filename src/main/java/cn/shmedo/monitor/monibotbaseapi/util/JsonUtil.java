package cn.shmedo.monitor.monibotbaseapi.util;

import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    public static <T> T toObject(String str, Class<T> tClass) {
        try {
            ObjectMapper objectMapper = ContextHolder.getBean(ObjectMapper.class);
            return objectMapper.readValue(str, tClass);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T toObject(String str, TypeReference<T> typeReference) {
        try {
            ObjectMapper objectMapper = ContextHolder.getBean(ObjectMapper.class);
            return objectMapper.readValue(str, typeReference);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String toJson(Object obj) {
        try {
            if (obj instanceof String) {
                return (String) obj;
            }
            ObjectMapper objectMapper = ContextHolder.getBean(ObjectMapper.class);
            return objectMapper.writeValueAsString(obj);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
