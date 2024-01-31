package cn.shmedo.monitor.monibotbaseapi.model.response;


import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectBaseInfo extends TbProjectInfo{

    private String projectTypeName;

    private String projectMainTypeName;

    private Integer waterWarn;


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
