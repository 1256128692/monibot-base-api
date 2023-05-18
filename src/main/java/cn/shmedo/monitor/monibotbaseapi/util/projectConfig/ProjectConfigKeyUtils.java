package cn.shmedo.monitor.monibotbaseapi.util.projectConfig;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.base.Tuple;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-16 10:59
 */
public class ProjectConfigKeyUtils {
    /**
     * 拼接对应key
     *
     * @param item     含key对象
     * @param targetID 被配置对象ID
     */
    public static void setKey(@NotNull IProjectConfigKey item, final Integer targetID, final boolean isProject) {
        if (!isProject) {
            Optional.of(item).filter(u -> ObjectUtil.isNotEmpty(u.getKey()) && !u.getKey().contains("::"))
                    .ifPresent(u -> u.setKey(u.getKey() + "::" + targetID));
        }
    }

    public static Tuple<String, Integer> getKey(final String key, final Integer projectID) {
        return Objects.nonNull(key) && key.contains("::") ?
                new Tuple<>(key.split("::")[0], Integer.parseInt(key.split("::")[1])) : new Tuple<>(key, projectID);
    }
}
