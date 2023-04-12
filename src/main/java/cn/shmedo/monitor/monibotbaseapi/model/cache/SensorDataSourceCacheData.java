package cn.shmedo.monitor.monibotbaseapi.model.cache;

import cn.shmedo.monitor.monibotbaseapi.model.enums.DataSourceComposeType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DatasourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 数据源
 *
 * @author Chengfs on 2023/3/23
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SensorDataSourceCacheData extends AbstractCacheData {

    /**
     * 数据源 雪花ID
     */
    private String dataSourceID;

    /**
     * 数据源类型 {@link DatasourceType}
     */
    private DatasourceType dataSourceType;

    /**
     * 数据源标识 1.物联网uniqueToken@物联网传感器名称  2.监测传感器名称
     */
    private String dataSourceToken;

    /**
     * 模板数据源标识，使用代号<br/>
     * 在公式中可以使用代号引用对应的数据源<br/>
     * 物联网传感器代号：203_a,203_b,999_a<br/>
     * 监测传感器代号：22_a
     */
    private String templateDataSourceToken;

    /**
     * 模板数据来源类型 {@link DataSourceComposeType}
     */
    private DataSourceComposeType dataSourceComposeType;

    /**
     * 扩展字段
     */
    private String exValues;

    private List<SensorCacheData> sensorInfoList;

}

    
    