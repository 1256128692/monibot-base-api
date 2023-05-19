package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-30 17:47
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorTypeFieldWithFormula extends TbMonitorTypeField {
    private String displayFormula;
    private String formula;
}
