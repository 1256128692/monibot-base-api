package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.MonitorTypeFieldV2;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-03-04 10:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorTypeFieldListV2Info extends TbMonitorType {
    private List<MonitorTypeFieldV2> fieldList;
}
