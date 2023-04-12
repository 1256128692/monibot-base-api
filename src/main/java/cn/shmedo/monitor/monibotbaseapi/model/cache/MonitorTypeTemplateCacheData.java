package cn.shmedo.monitor.monibotbaseapi.model.cache;

import cn.hutool.core.bean.BeanUtil;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeTemplate;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbTemplateDataSource;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbTemplateFormula;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbTemplateScript;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CalType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.DataSourceComposeType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Collections;
import java.util.List;

/**
 * 监测类型模板
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
public class MonitorTypeTemplateCacheData extends AbstractCacheData {
    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板数据来源类型
     */
    private DataSourceComposeType dataSourceComposeType;

    /**
     * 监测类型模板分布式唯一ID
     */
    private String templateDataSourceID;

    /**
     * 监测类型
     */
    private Integer monitorType;

    /**
     * 计算类型
     */
    private CalType calType;

    /**
     * 排序字段
     */
    private Integer displayOrder;

    /**
     * 扩展字段
     */
    private String exValues;

    /**
     * 创建类型
     */
    private Integer createType;

    /**
     * 公司ID
     */
    private Integer companyID;

    /**
     * 是否是默认模板<br/>
     * 对于单一物模型，单一物联网触感其的模板，是否作为默认模板使用。<br/>
     * 如果是默认模板，来了该单一物联网传感器以后，自动创建监测传感器。<br/>
     * 其他数据源组合类型都是0
     */
    private Boolean defaultTemplate;

    /**
     * 模板数据源列表
     */
    private List<TemplateDataSourceCacheData> templateDataSourceList;

    /**
     * 模板公式列表
     */
    private List<FormulaCacheData> templateFormulaList;

    /**
     * 模板脚本列表
     */
    private List<ScriptCacheData> templateScriptList;

    public void setTemplateScriptList(List<ScriptCacheData> templateScriptList) {
        this.templateScriptList = templateScriptList != null ? templateScriptList : Collections.emptyList();
    }

    public void setTemplateFormulaList(List<FormulaCacheData> templateFormulaList) {
        this.templateFormulaList = templateFormulaList != null ? templateFormulaList : Collections.emptyList();
    }

    public static MonitorTypeTemplateCacheData valueOf(TbMonitorTypeTemplate template, List<TbTemplateDataSource> sources,
                                                       List<TbTemplateFormula> formulaList, List<TbTemplateScript> scriptList) {
        MonitorTypeTemplateCacheData cacheData = new MonitorTypeTemplateCacheData();
        BeanUtil.copyProperties(template, cacheData);
        List<TemplateDataSourceCacheData> sourceList = BeanUtil.copyToList(sources, TemplateDataSourceCacheData.class);
        List<FormulaCacheData> templateFormulaList = BeanUtil.copyToList(formulaList, FormulaCacheData.class);
        List<ScriptCacheData> templateScriptList = BeanUtil.copyToList(scriptList, ScriptCacheData.class);
        return cacheData.toBuilder()
                .templateDataSourceList(sourceList)
                .templateFormulaList(templateFormulaList)
                .templateScriptList(templateScriptList)
                .build();
    }
}

    
    