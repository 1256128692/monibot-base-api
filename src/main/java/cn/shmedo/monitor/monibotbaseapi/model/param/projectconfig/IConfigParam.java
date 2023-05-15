package cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-12 18:10
 * @desc: 自定义配置处理入参定义
 */
public interface IConfigParam {
    Integer getProjectID();

    void setProjectID(final Integer projectID);

    String getGroup();

    void setGroup(final String group);

    String getKey();

    void setKey(final String key);

    String getValue();

    void setValue(final String value);
}
