package cn.shmedo.monitor.monibotbaseapi.model.enums;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.base.Tuple;
import cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig.IConfigID;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-15 11:06
 * @desc: tb_project_config配置分级的枚举，需要根据{@code IConfigID}的成员变量来定
 * @see IConfigID
 */
public enum ProjectGroupType {
    PROJECT("project", "工程"),
    MONITOR_GROUP("monitorGroup", "监测点分组"),
    MONITOR_POINT("monitorPoint", "监测点");

    private final String code;
    private final String desc;

    ProjectGroupType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static ProjectGroupType getProjectGroupType(IConfigID data) {
        List<String> list = Arrays.stream(IConfigID.class.getMethods())
                .filter(u -> u.getName().startsWith("get") && u.getName().endsWith("ID")).map(u -> {
                    try {
                        u.setAccessible(true);
                        return new Tuple<>(u, u.invoke(data));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException("invoke failure,data: " + data.toString(null), e);
                    }
                }).filter(u -> Objects.nonNull(u.getItem2())).map(Tuple::getItem1).map(Method::getName)
                .map(u -> u.substring(3, u.length() - 2)).distinct().toList();
        Assert.isTrue(CollectionUtil.isNotEmpty(list), () -> new IllegalArgumentException("Param need set an 'ID' at least"));
        String dCode = list.size() == 1 ? list.get(0) : list.stream().filter(u -> !u.equalsIgnoreCase(PROJECT.code))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Unreachable Exception"));
        return Arrays.stream(values()).filter(u -> u.code.equalsIgnoreCase(dCode)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(dCode + "not exists in enum."));
    }
}
