package cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-22 13:33
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorPointWithItemBaseInfo extends TbMonitorPoint {
    private Integer monitorItemID;
    private String monitorItemName;
    private String monitorItemAlias;
}
