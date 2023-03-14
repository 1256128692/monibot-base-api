package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.ResultWrapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QueryMonitorPointListParam implements ParameterValidator {

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    private Integer projectTypeID;

    private Integer monitorType;

    private Integer monitorItemID;

    private String town;

    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

}
