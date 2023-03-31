package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ParamSubjectType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-03-30 16:17
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryParamParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotNull
    private Integer subjectType;
    @NotEmpty
    @Valid
    @Size(max = 100)
    private List< @NotBlank String> subjectTokenList;
    @Override
    public ResultWrapper validate() {
        if (ParamSubjectType.isValid(subjectType)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "参数类型不合法符");
        }
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
