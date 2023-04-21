package cn.shmedo.monitor.monibotbaseapi.model.standard;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-21 10:29
 */
public interface IFieldName extends IFieldToken, IMonitorType {
    void setFieldName(String metadataName);

    String getFieldName();
}
