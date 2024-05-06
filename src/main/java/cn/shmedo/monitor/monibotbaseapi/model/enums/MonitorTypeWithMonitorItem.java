package cn.shmedo.monitor.monibotbaseapi.model.enums;

import cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem.MonitorItemWithDefaultChecked;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 监测类型是否被监测项目引用枚举
 * @program: monibot-base-api
 * @create: 2023-10-16 10:28
 **/
@Getter
@AllArgsConstructor
public enum MonitorTypeWithMonitorItem {
    ALL(0, "全部"),
    PART(1, "被引用"),
    NONE(2, "未被引用")
    ;

    private final Integer type;
    private final String typeStr;

    public static MonitorTypeWithMonitorItem getMonitorTypeWithMonitorItem(Integer type){
        for (MonitorTypeWithMonitorItem value : MonitorTypeWithMonitorItem.values()) {
            if(value.type.equals(type))
                return value;
        }
        return null;
    }
}
