package cn.shmedo.monitor.monibotbaseapi.model.param.monitorItem;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.Optional;

/**
 * @author Chengfs on 2023/12/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryMonitorItemSimpleListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    @Positive
    private Integer companyID;

    @NotNull
    @Positive
    private Integer projectID;

    @Positive
    private Integer monitorType;

    private Boolean enable;

    @Range(min = 0, max = 1)
    private Integer createType;


    @Override
    public ResultWrapper<?> validate() {
        enable = Optional.ofNullable(enable).orElse(true);
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}