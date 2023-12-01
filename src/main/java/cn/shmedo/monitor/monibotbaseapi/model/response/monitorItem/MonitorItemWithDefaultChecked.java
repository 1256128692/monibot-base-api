package cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-11-07 11:36
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class MonitorItemWithDefaultChecked extends TbMonitorItem {
    private Boolean defaultChecked;
}
