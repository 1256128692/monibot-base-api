package cn.shmedo.monitor.monibotbaseapi.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 属性主体类型枚举
 * @program: monibot-base-api
 * @create: 2023-10-16 10:28
 **/
@Getter
@AllArgsConstructor
public enum FavoriteSubjectType {
    MONITOR_PROJECT(0, "监测项目");

    private final Integer type;
    private final String typeStr;

}
