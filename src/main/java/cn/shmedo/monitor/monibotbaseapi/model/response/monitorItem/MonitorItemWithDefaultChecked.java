package cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-11-07 11:36
 **/
@Data
public class MonitorItemWithDefaultChecked extends TbMonitorItem {
    private Boolean defaultChecked;
}
