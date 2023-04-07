package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbParameter;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
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
    private List<String> dataSourceNames;

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
    }

}

    
    