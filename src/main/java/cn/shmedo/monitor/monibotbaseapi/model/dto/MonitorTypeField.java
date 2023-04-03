package cn.shmedo.monitor.monibotbaseapi.model.dto;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Chengfs on 2023/4/3
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorTypeField extends TbMonitorTypeField {

    /**
     * 字段值
     */
    private String value;

    /**
     * 公式
     */
    private String formula;
}

    
    