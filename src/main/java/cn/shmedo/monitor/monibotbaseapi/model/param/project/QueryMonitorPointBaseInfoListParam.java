package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.ResultWrapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QueryMonitorPointBaseInfoListParam implements ParameterValidator {

    @NotNull(message = "工程ID不能为空")
    private Integer projectID;


    @Override
    public ResultWrapper validate() {
        return null;
    }
}
