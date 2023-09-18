package cn.shmedo.monitor.monibotbaseapi.model.param.file;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

/**
 * @Author wuxl
 * @Date 2023/9/18 15:07
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.param.file
 * @ClassName: DownLoadFileParameter
 * @Description: TODO
 * @Version 1.0
 */
@Data
@ToString
public class DownLoadFileParameter implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    @NotNull(message = "文件ID不能为空")
    private Integer fileID;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
