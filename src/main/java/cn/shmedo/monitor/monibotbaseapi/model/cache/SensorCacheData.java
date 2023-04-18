package cn.shmedo.monitor.monibotbaseapi.model.cache;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.monitor.enums.DataSourceComposeType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 监测传感器
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SensorCacheData extends AbstractCacheData {

    /**
     * 项目ID
     */
    private Integer projectID;

    /**
     * 监测类型模板ID
     */
    private Integer templateID;

    /**
     * 数据源ID
     */
    private String dataSourceID;

    /**
     * 模板数据来源类型 {@link DataSourceComposeType}
     */
    private DataSourceComposeType dataSourceComposeType;

    /**
     * 监测类型
     */
    private Integer monitorType;

    /**
     * 传感器名称
     */
    private String name;

    /**
     * 传感器别名
     */
    private String alias;

    /**
     * 传感器类型<br/>
     * 1-自动化传感器 2-融合传感器 3-人工传感器
     */
    private Integer kind;

    /**
     * 显示排序
     */
    private Integer displayOrder;

    /**
     * 关联监测点
     */
    private String monitorPointID;

    /**
     * 监测类型拓展配置值
     */
    private Dict configFieldValue;

    /**
     * 扩展字段
     */
    private Dict exValues;

    /**
     * 监测类型
     */
    private MonitorType type;

    /**
     * 监测类型模板
     */
    private MonitorTypeTemplateCacheData template;

    public void setConfigFieldValue(String configFieldValue) {
        this.configFieldValue = JSONUtil.toBean(configFieldValue, Dict.class);
    }

    public void setExValues(String exValues) {
        this.exValues = JSONUtil.toBean(exValues, Dict.class);
    }
}

    
    