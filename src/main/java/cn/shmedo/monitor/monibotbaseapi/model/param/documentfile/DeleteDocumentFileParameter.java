package cn.shmedo.monitor.monibotbaseapi.model.param.documentfile;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Objects;

/**
 * @Author wuxl
 * @Date 2023/9/18 14:24
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.param.file
 * @ClassName: FileListParameter
 * @Description: TODO
 * @Version 1.0
 */
@Data
@ToString
public class DeleteDocumentFileParameter implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    private Integer projectID;

    @NotEmpty(message = "文件ID列表不能为空")
    private List<Integer> fileIDList;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        if (Objects.nonNull(projectID))
            return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
