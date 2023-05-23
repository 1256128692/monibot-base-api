package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

/**
 * 刷新监测类型缓存请求体
 *
 * @author Chengfs on 2023/5/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshMonitorTypeCacheParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    private Boolean isClear;

    @NotNull
    private Integer companyID;

    @Override
    public ResultWrapper<?> validate() {
        this.isClear = Optional.ofNullable(isClear).orElse(Boolean.TRUE);
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}