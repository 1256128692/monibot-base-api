package cn.shmedo.monitor.monibotbaseapi.model.param.project;


import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class QuerySmcPointHistoryDataListParam extends QueryMonitorPointSensorDataListParam implements ParameterValidator {

    @JsonIgnore
    private Integer monitorType;

    @Override
    public ResultWrapper validate() {

//        if ( !this.getTbMonitorPoint().getMonitorType().equals(MonitorType.SOIL_MOISTURE.getKey())) {
//            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前监测点不是墒情类型");
//        }else {
//            monitorType = this.getTbMonitorPoint().getMonitorType();
//        }
        return null;
    }
}
