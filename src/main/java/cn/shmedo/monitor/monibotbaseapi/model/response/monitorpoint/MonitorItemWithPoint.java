package cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-13 14:00
 **/
@Data
public class MonitorItemWithPoint {
    private Integer monitorItemID;
    private String monitorItemName;
    private String monitorItemAlias;
    private Integer monitorType;
    private String monitorTypeName;
    private String monitorTypeAlias;
    private List<TbMonitorPoint> monitorPointList;
}
