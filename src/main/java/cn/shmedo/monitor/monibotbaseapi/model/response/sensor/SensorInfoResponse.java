package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensorDataSource;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 传感器详情 响应体
 *
 * @author Chengfs on 2023/4/3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SensorInfoResponse extends TbSensor {

    /**
     * 数据源名称
     */
    private List<TbSensorDataSource> dataSourceList;

    /**
     * 监测类型名称
     */
    private String monitorTypeName;

    /**
     * 扩展字段
     */
    private List<ExField> exFields;

    /**
     * 参数
     */
    private List<TbParameter> paramFields;

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ExField extends TbMonitorTypeField {
        private String value;

        public static ExField valueOf(TbMonitorTypeField field) {
            ExField exField = new ExField();
            exField.setID(field.getID());
            exField.setMonitorType(field.getMonitorType());
            exField.setFieldToken(field.getFieldToken());
            exField.setFieldName(field.getFieldName());
            exField.setFieldDataType(field.getFieldDataType());
            exField.setFieldClass(field.getFieldClass());
            exField.setFieldDesc(field.getFieldDesc());
            exField.setFieldUnitID(field.getFieldUnitID());
            exField.setParentID(field.getParentID());
            exField.setCreateType(field.getCreateType());
            exField.setExValues(field.getExValues());
            exField.setDisplayOrder(field.getDisplayOrder());
            return exField;
        }

        public static ExField valueOf(TbMonitorTypeField field, String value) {
            ExField exField = valueOf(field);
            exField.setValue(value);
            return exField;
        }
    }

    public static SensorInfoResponse valueOf(TbSensor sensor) {
        SensorInfoResponse response = new SensorInfoResponse();
        response.setID(sensor.getID());
        response.setProjectID(sensor.getProjectID());
        response.setTemplateID(sensor.getTemplateID());
        response.setDataSourceID(sensor.getDataSourceID());
        response.setDataSourceComposeType(sensor.getDataSourceComposeType());
        response.setMonitorType(sensor.getMonitorType());
        response.setName(sensor.getName());
        response.setAlias(sensor.getAlias());
        response.setKind(sensor.getKind());
        response.setDisplayOrder(sensor.getDisplayOrder());
        response.setMonitorPointID(sensor.getMonitorPointID());
        response.setConfigFieldValue(sensor.getConfigFieldValue());
        response.setExValues(sensor.getExValues());
        response.setStatus(sensor.getStatus());
        response.setWarnNoData(sensor.getWarnNoData());
        response.setMonitorBeginTime(sensor.getMonitorBeginTime());
        response.setImagePath(sensor.getImagePath());
        response.setEnable(sensor.getEnable());
        response.setCreateTime(sensor.getCreateTime());
        response.setUpdateTime(sensor.getUpdateTime());
        response.setCreateUserID(sensor.getCreateUserID());
        response.setUpdateUserID(sensor.getUpdateUserID());
        return response;
    }
}

    
    