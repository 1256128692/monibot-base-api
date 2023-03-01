package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProjectImageParam {
    private Integer projectID;

    @NotBlank
    private String imageContent;
    @NotBlank
    private String imageSuffix;
}
