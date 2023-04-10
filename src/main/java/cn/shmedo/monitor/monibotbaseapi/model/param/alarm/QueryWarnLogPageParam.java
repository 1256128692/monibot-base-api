package cn.shmedo.monitor.monibotbaseapi.model.param.alarm;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class QueryWarnLogPageParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @Min(value = 1, message = "公司ID不能小于1")
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;
    @Size(max = 2)
    @NotNull(message = "查询类型不能为空 查询类型 0.预留 1.实时记录 2.历史记录")
    private Integer queryType;
    private String queryCode;
    private Integer monitorTypeID;
    private Integer monitorItemID;
    private Integer alarmLevel;
    private Integer orderType;
    private Integer currentPage;
    private Integer pageSize;

    @Override
    public ResultWrapper validate() {

        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.companyID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionProvider.super.resourcePermissionType();
    }
}
