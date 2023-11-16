package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-16 10:00
 */
@Data
public class AddManualDataBatchParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "工程ID不能为空!")
    @Positive(message = "工程ID不能小于1")
    private Integer projectID;
    @NotEmpty(message = "数据列表不能为空")
    private List<ManualDataItem> dataList;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    public static AddManualDataBatchParam build() {
        //TODO
        return null;
    }
}
