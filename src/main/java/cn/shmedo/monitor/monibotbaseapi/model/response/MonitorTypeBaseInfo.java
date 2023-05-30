package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import lombok.Data;

import java.util.List;

@Data
public class MonitorTypeBaseInfo {

    private Integer monitorType;
    private String monitorTypeName;
    private String monitorTypeAlias;
    private Integer pointCount;
    private WarnInfo warnInfo;
    private List<MonitorItemBaseInfo> monitorItemList;

    private List<TbProjectType> projectTypeList;

    private Integer displayOrder;

}
