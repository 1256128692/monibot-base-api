package cn.shmedo.monitor.monibotbaseapi.model.response;


import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectBaseInfo extends TbProjectInfo{

    private String projectTypeName;

    private String projectMainTypeName;

    private Integer waterWarn;

    private List<TbMonitorItem> tbMonitorItemList;


    @Override
    public void setProjectType(Byte projectType) {
        TbProjectType type = ProjectTypeCache.projectTypeMap.getOrDefault(projectType, null);
        if(type != null) {
            this.projectTypeName = type.getTypeName();
            this.projectMainTypeName = type.getMainType();
        }
        super.setProjectType(projectType);
    }

}
