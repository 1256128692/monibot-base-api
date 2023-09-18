package cn.shmedo.monitor.monibotbaseapi.model.param.file;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import org.springframework.http.MediaType;

/**
 * @Author wuxl
 * @Date 2023/9/18 14:35
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.param.file
 * @ClassName: AddFileParameter
 * @Description: TODO
 * @Version 1.0
 */
@Data
@ToString
public class AddFileParameter implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    @NotNull(message = "关联ID不能为空")
    private Integer correlationID;

    @NotBlank(message = "文件名称不能为空")
    private String fileName;

    @NotBlank(message = "文件类型不能为空")
    private String fileType;

    @NotNull(message = "文件大小不能为空")
    private Integer fileSize;

    @NotBlank(message = "文件路径不能为空")
    private String filePath;

    private String fileDesc;

    private String exValue;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
