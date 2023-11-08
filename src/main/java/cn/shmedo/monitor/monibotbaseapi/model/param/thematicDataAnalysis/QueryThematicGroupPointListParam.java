package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitoringItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import org.hibernate.validator.constraints.Range;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-08 15:09
 */
@Data
public class QueryThematicGroupPointListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "工程ID不能为空!")
    @Positive(message = "工程ID不能小于1")
    private Integer projectID;
    @Range(max = 1, min = 1, message = "专题类型 1.浸润线 默认1.浸润线")
    private Integer thematicType;
    @JsonIgnore
    private String monitorItemName;

    @Override
    public ResultWrapper validate() {
        // 目前只有一个'浸润线'，因此直接在这里设置;否则需要通过{@code thematicType}来确认
        monitorItemName = MonitoringItem.WET_LINE.getValue();
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
