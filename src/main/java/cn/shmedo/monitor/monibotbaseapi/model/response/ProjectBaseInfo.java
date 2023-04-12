package cn.shmedo.monitor.monibotbaseapi.model.response;


import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import lombok.Data;

import java.util.Map;

@Data
public class ProjectBaseInfo {

    private Integer id;

    private String projectName;

    private String shortName;

    private Byte projectType;

    private String projectTypeName;

    private String projectMainTypeName;

    private String imagePath;


    public static ProjectBaseInfo toNewVo(TbProjectInfo item, Map<Byte, TbProjectType> projectTypeMap) {

        ProjectBaseInfo vo = new ProjectBaseInfo();
        vo.setId(item.getID());
        vo.setProjectName(item.getProjectName());
        vo.setShortName(item.getShortName());
        vo.setProjectType(item.getProjectType());
        vo.setImagePath(item.getImagePath());
        vo.setProjectTypeName(projectTypeMap.get(item.getProjectType()).getTypeName());
        vo.setProjectMainTypeName(projectTypeMap.get(item.getProjectType()).getMainType());
        return vo;
    }
}
