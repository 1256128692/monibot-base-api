package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-03-04 10:52
 */
@Data
public class QueryMonitorTypeFieldListV2Param implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID必须为正值")
    private Integer companyID;
    private Boolean allFiled;
    @JsonIgnore
    private List<Integer> fieldClassList;

    @Override
    public ResultWrapper<?> validate() {
        allFiled = Objects.nonNull(allFiled) && allFiled;
        fieldClassList = allFiled ? null : List.of(FieldClass.BASIC.getCode(), FieldClass.EXTEND.getCode());
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
