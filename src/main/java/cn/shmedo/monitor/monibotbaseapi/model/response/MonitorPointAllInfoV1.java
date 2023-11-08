package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroupItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class MonitorPointAllInfoV1 {

    private Integer monitorItemID;
    private String monitorItemName;
    private String monitorItemAlias;
    private Integer monitorType;
    private String monitorTypeName;
    private String monitorTypeAlias;

    @JsonIgnore
    private List<TbMonitorGroupItem> monitorGroupItemList;
    private List<MonitorGroupBaseInfoV1> monitorGroupList;
    private List<MonitorPointBaseInfoV1> monitorPointList;

}
