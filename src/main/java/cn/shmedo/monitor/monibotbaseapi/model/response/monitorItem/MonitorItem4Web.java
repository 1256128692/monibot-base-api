package cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-11 10:26
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorItem4Web extends TbMonitorItem {
    private String typeName;
    private String typeAlias;


    private List<TbMonitorTypeFieldWithItemID> fieldList;
}
