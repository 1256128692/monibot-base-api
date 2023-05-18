package cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig;

import cn.hutool.json.JSONUtil;
import jakarta.annotation.Nullable;

import java.util.Map;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-15 11:08
 * @desc: 该接口定义配置入参可定义的ID(目前仅有monitorGroupID)，但是这种ID只能被定义一个
 * 比如假设本接口里有多个getA1ID(),getA2ID(),...方法，那么这些方法中仅有一个是有值的，其他值均为null
 */
public interface IConfigID {
    Integer getProjectID();

    void setProjectID(final Integer projectID);

    Integer getMonitorGroupID();

    void setMonitorGroupID(final Integer monitorGroupID);

    Integer getMonitorPointID();

    void setMonitorPointID(final Integer monitorPointID);

    /**
     * @param obj meaningless,as a placeholder to let method 'toString' could be written here
     */
    default String toString(@Nullable Object obj) {
        return JSONUtil.toJsonStr(Map.of("monitorGroupID", getMonitorGroupID()));
    }
}
