package cn.shmedo.monitor.monibotbaseapi.model.cache;

import cn.shmedo.monitor.monibotbaseapi.model.enums.DatasourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TemplateDataSourceCacheData extends AbstractCacheData {

    /**
     * 监测类型模板ID
     */
    private String templateDataSourceID;

    /**
     * 数据源类型
     */
    private DatasourceType dataSourceType;

    /**
     * 模板数据源标识，使用代号<br/>
     * 在公式中可以使用代号引用对应的数据源<br/>
     * 物联网传感器代号：203_a,203_b,999_a<br/>
     * 监测传感器代号：22_a
     */
    private String templateDataSourceToken;
}