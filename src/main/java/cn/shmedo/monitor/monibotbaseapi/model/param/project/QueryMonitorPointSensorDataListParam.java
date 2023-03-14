package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.ResultWrapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class QueryMonitorPointSensorDataListParam implements ParameterValidator {


    private Integer projectID;

    private Integer monitorPointID;

    @NotNull
    private Date begin;

    @NotNull
    private Date end;

    @NotBlank
    private String density;


    @Override
    public ResultWrapper<?> validate() {
        return null;
    }
}
