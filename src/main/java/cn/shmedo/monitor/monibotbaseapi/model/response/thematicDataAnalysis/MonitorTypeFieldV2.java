package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-16 11:15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorTypeFieldV2 extends TbMonitorTypeField {
    private String monitorTypeName;
    private String chnUnit;
    private String engUnit;
}
