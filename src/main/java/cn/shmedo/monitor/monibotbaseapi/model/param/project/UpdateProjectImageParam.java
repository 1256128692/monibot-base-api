package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import lombok.Data;

@Data
public class UpdateProjectImageParam {
    private Integer projectID;
    private String imageContent;
    private String imageSuffix;
}
