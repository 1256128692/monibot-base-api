package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-30 17:47
 **/
@Data
public class MonitorTypeFieldWithFormula extends TbMonitorTypeField {
    private String displayFormula;
    private String formula;
}
