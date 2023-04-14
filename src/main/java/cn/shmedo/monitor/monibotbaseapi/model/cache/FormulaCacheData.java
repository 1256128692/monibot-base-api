package cn.shmedo.monitor.monibotbaseapi.model.cache;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FormulaCacheData extends AbstractCacheData {

    /**
     * 监测类型模板ID
     */
    private Integer templateID;

    /**
     * 监测类型
     */
    private Integer monitorType;

    /**
     * 字段ID
     */
    private Integer fieldID;

    /**
     * 公式表达式
     */
    private String formula;
}