package cn.shmedo.monitor.monibotbaseapi.model.cache;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ScriptCacheData extends AbstractCacheData {

    /**
     * 监测类型模板ID
     */
    private Integer templateID;

    /**
     * 监测类型
     */
    private Integer monitorType;

    /**
     * 脚本内容
     */
    private String script;
}