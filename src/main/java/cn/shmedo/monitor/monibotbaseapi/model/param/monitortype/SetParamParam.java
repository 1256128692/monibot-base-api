package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ParamSubjectType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-30 14:28
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetParamParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NonNull
    private Integer companyID;
    @NonNull
    private Integer subjectType;
    private Boolean deleteOnly;
    @Valid
    @NotEmpty
    @Size(max = 100)
    private List<@NonNull ParamItem> paramList;

    @Override
    public ResultWrapper validate() {
        if (!ParamSubjectType.isValid(subjectType)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,"参数类型有误");
        }
        if (deleteOnly != null && deleteOnly){
            if (paramList.stream().allMatch(item -> item.getID() == null)){
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER,"删除请提供ID");
            }
        }
        // TODO 校验paramList
        return null;
    }

    @Override
    public Resource parameter() {
        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionProvider.super.resourcePermissionType();
    }
}
