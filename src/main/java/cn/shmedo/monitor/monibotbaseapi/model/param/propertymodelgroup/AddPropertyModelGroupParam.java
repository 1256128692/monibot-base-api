package cn.shmedo.monitor.monibotbaseapi.model.param.propertymodelgroup;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

/**
 * @Author wuxl
 * @Date 2023/9/15 17:14
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.param.propertymodelgroup
 * @ClassName: AddPropertyModelGroup
 * @Description: TODO
 * @Version 1.0
 */
@Data
@ToString
public class AddPropertyModelGroupParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @NotBlank(message = "所属平台不能为空")
    private String platform;

    @NotNull(message = "模板组类型不能为空")
    private Integer groupType;

    private Integer groupTypeSubType;

    @NotBlank(message = "名称不能为空")
    private String name;

    private String desc;

    private String exValue;

    @Override
    public ResultWrapper validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
