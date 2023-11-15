package cn.shmedo.monitor.monibotbaseapi.model.param.property;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyModelType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 17:08
 **/
@Data
@ToString
public class DeletePropertyValuesParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @NotNull(message = "对象类型不能为空")
    private Integer subjectType;

    @NotNull(message = "对象ID不能为空")
    private List<Long> subjectIDList;

    @Override
    public ResultWrapper<?> validate() {
        if(!PropertyModelType.WORK_FLOW.getCode().equals(subjectType)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "暂时只支持工作流表单数据删除");
        }
        return null;
    }

    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

}
