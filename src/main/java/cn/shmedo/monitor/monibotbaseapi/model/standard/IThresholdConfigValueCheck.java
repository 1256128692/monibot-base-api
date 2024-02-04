package cn.shmedo.monitor.monibotbaseapi.model.standard;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapWrapper;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-18 18:01
 */
public interface IThresholdConfigValueCheck {
    /**
     * @throws IllegalArgumentException 携带报错message的{@code IllegalArgumentException}
     */
    default void validateValue(final String value, @NotNull final Boolean enable, final Set<Integer> warnLevelSet,
                               final List<String> configKeyList) throws IllegalArgumentException {
        if (ObjectUtil.isNotEmpty(value)) {
            try {
                // e.g. {"1":{"upper":100,"lower":50},"2":{"upper":50,"lower":25},...}
                JSONObject object = JSONUtil.parseObj(value);
                Set<String> configWarnLevel = object.keySet();
                // 如果配置的json不合法,在这里直接报错
                boolean configStatus = queryConfigStatus(value, true);
                if (enable && !configStatus) {
                    throw new IllegalArgumentException("未配置完全的报警阈值配置无法启用");
                }
                if (configWarnLevel.stream().map(Integer::valueOf).anyMatch(u -> !warnLevelSet.contains(u))) {
                    throw new IllegalArgumentException("配置的报警等级阈值和平台报警等级枚举设置不匹配");
                }
                if (object.values().stream().map(JSONUtil::parseObj).map(MapWrapper::keySet).anyMatch(u -> !u.containsAll(configKeyList))) {
                    throw new IllegalArgumentException("配置的报警等级阈值和比较方式不匹配");
                }
            } catch (JSONException e) {
                throw new IllegalArgumentException("配置的报警等级阈值不合法");
            }
        }
    }

    /**
     * 查询配置情况是否和{@code status}字段一致<br>
     * 只要配置了某一级报警阈值,就是'已配置'
     *
     * @param value  报警配置json
     * @param status 查询的状态,true|false 已配置|未配置
     * @return true:符合查询的状态{@code status}; false:不符合
     * @throws JSONException 解析json失败
     */
    default boolean queryConfigStatus(@Nullable final String value, @NotNull final Boolean status) throws JSONException {
        return !Optional.ofNullable(value).filter(ObjectUtil::isNotEmpty).map(JSONUtil::parseObj).map(MapWrapper::entrySet)
                .filter(CollUtil::isNotEmpty).map(u -> status != u.stream().anyMatch(w -> Optional.ofNullable(w.getValue())
                        .filter(ObjectUtil::isNotEmpty).map(JSONUtil::parseObj).map(MapWrapper::entrySet)
                        .filter(CollUtil::isNotEmpty).map(s -> s.stream().map(Map.Entry::getValue).allMatch(n ->
                                ObjectUtil.isNotEmpty(n) && !"null".equals(n))).orElse(false))).orElse(status);
    }
}
