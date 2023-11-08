package cn.shmedo.monitor.monibotbaseapi.util;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
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

    /**
     * 从JSON字符串中获取节点
     *
     * @param json JSON字符串
     * @param key  节点key
     * @return {@link JsonNode}
     */
    public static @Nullable JsonNode getNode(String json, String key) {
        if (json == null || key == null) return null;
        JsonNode value = null;
        try {
            value = ContextHolder.getBean(ObjectMapper.class).readTree(json).get(key);
        } catch (JsonProcessingException e) {
            log.error(ExceptionUtil.stacktraceToString(e));
        }
        return value;
    }

    /**
     * 从JSON字符串中获取key的值
     *
     * @param json  JSON字符串
     * @param key   节点key
     * @param clazz 值的类型
     * @return 值
     */
    public static <T> @Nullable T get(String json, String key, Class<T> clazz) {
        if (json == null || key == null) return null;
        try {
            JsonNode valueNode = getNode(json, key);
            if (valueNode != null) {
                return ContextHolder.getBean(ObjectMapper.class).readValue(valueNode.traverse(), clazz);
            }
        } catch (IOException e) {
            log.error(ExceptionUtil.stacktraceToString(e));
        }
        return null;
    }

    /**
     * 从JSON字符串中获取值为String的key的值<br/>
     * 当值不为String时，返回值的原始字符串
     *
     * @param json JSON字符串
     * @param key  节点key
     * @return 值
     */
    public static @Nullable String getStr(String json, String key) {
        if (json == null || key == null) return null;
        JsonNode valueNode = getNode(json, key);
        if (valueNode != null) {
            return valueNode.isTextual() ? valueNode.textValue() : valueNode.toString();
        }
        return null;
    }
}
