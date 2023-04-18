package cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroup;
import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-18 13:51
 **/
@Data
public class Group4Web extends TbMonitorGroup {
    private String imgURL;
    List<GroupMonitorItem> monitorItemList;
    List<GroupPoint> monitorPointList;
    List<Group4Web> childGroupList;
}
