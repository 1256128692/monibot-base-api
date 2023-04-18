package cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup;

import lombok.Data;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-18 14:19
 **/
@Data
public class GroupPoint {
private Integer monitorPointID;
    private String monitorPointName;
    private Integer monitorType;
    private Integer monitorItemID;
    private String imageLocation;
    private Integer groupID;
}
