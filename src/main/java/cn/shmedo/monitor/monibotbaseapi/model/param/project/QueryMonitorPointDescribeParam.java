package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.ResultWrapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QueryMonitorPointDescribeParam implements ParameterValidator {

    @NotNull(message = "项目ID不能为空")
    private Integer projectID;

    @NotNull(message = "监测点ID不能为空")
    private Integer monitorPointID;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }
}
