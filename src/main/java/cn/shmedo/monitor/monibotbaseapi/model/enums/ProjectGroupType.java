package cn.shmedo.monitor.monibotbaseapi.model.enums;

import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig.IConfigGroup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-15 11:06
 * @desc: tb_project_config配置分级的枚举，需要根据{@code IConfigGroup}的入参来定
 */
public enum ProjectGroupType {
    MONITOR_GROUP("monitorGroup", "监测点分组");

    private final String code;
    private final String desc;

    ProjectGroupType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ProjectGroupType getProjectGroupType(IConfigGroup data) {
        String code = Arrays.stream(IConfigGroup.class.getMethods())
                .filter(u -> u.getName().startsWith("get") && u.getName().endsWith("ID")).map(u -> {
                    try {
                        u.setAccessible(true);
                        return new Tuple<>(u, u.invoke(data));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("invoke failure,data: " + data.toString(null), e);
                    }
                }).filter(u -> Objects.nonNull(u.getItem2())).map(Tuple::getItem1).map(Method::getName).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("param need set an 'ID' at least"));
        return Arrays.stream(values()).filter(u -> u.code.equalsIgnoreCase(code)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(code + "not exists in enum."));
    }
}
