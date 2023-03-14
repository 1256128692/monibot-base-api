package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.ResultWrapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatisticsReservoirMonitorPointTypeParam implements ParameterValidator {

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }
}
