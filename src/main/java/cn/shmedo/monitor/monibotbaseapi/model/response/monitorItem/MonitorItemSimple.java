package cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import lombok.Data;

/**
 * @author Chengfs on 2023/12/18
 */
@Data
public class MonitorItemSimple {

    private Integer id;
    private String name;
    private String alias;
    private Integer monitorType;
    private Integer projectID;
    private Byte createType;
    private Boolean enable;

    public static  MonitorItemSimple valueOf(TbMonitorItem monitorItem){
        MonitorItemSimple monitorItemSimple = new MonitorItemSimple();
        monitorItemSimple.setId(monitorItem.getID());
        monitorItemSimple.setName(monitorItem.getName());
        monitorItemSimple.setAlias(monitorItem.getAlias());
        monitorItemSimple.setMonitorType(monitorItem.getMonitorType());
        monitorItemSimple.setProjectID(monitorItem.getProjectID());
        monitorItemSimple.setCreateType(monitorItem.getCreateType());
        monitorItemSimple.setEnable(monitorItem.getEnable());
        return monitorItemSimple;
    }
}