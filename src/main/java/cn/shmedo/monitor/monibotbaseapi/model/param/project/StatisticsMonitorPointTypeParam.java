package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.ResultWrapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatisticsMonitorPointTypeParam implements ParameterValidator {

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    @NotNull(message = "查询类型不能为空")
    private Integer queryType;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }
}
