package cn.shmedo.monitor.monibotbaseapi.model.param.favorite;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class DeleteFavoriteParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司id不能为空")
    private Integer companyID;
    @NotEmpty(message = "收藏id不能为空")
    private List<Integer> IDList;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

}
