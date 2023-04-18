package cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup;

import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-18 14:18
 **/
@Data
public class GroupMonitorItem {
    private Integer monitorItemID;
    private String monitorItemName;
    private String monitorItemAlias;
    private Integer groupID;
}
