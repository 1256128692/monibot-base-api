package cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig;

import cn.hutool.json.JSONException;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-15 14:59
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class DataWarnConfigInfo extends DeviceWarnConfigInfo {
    @JsonIgnore
    private String warnLevel;

    @JsonProperty("warnLevel")
    private List<Integer> warnLevel() {
        try {
            return Optional.ofNullable(warnLevel).map(JSONUtil::parseArray).map(u -> JSONUtil.toList(u, Integer.class)).orElse(List.of());
        } catch (JSONException e) {
            log.error("parse json error,warnLevel: {}", warnLevel);
            return List.of();
        }
    }
}
