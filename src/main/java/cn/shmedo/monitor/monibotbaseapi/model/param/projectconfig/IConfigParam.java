package cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig;

import cn.shmedo.monitor.monibotbaseapi.util.projectConfig.IProjectConfigKey;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-12 18:10
 * @desc: 自定义配置处理入参定义
 */
public interface IConfigParam extends IProjectConfigKey {
    Integer getProjectID();

    void setProjectID(final Integer projectID);

    String getGroup();

    void setGroup(final String group);

    String getValue();

    void setValue(final String value);
}
