package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeTemplate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 数据源下拉 响应体
 *
 * @author Chengfs on 2023/4/6
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DataSourceCatalogResponse extends TbMonitorTypeTemplate {

    private List<DataSource> dataSourceList;

    /**
     * 监测类型 字段总数
     */
    @JsonIgnore
    private Integer fieldCount;

    /**
     * 已配置的公式总数
     */
    @JsonIgnore
    private Integer formulaCount;

    /**
     * 已配置的脚本总数
     */
    @JsonIgnore
    private Integer scriptCount;


    @Data
    public static class DataSource {

        private Integer dataSourceType;

        private List<?> childList;

        private String templateDataSourceToken;
    }
}

    
    