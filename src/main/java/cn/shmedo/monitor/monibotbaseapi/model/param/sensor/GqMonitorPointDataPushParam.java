package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ProjectType;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class GqMonitorPointDataPushParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;
    @NotNull(message = "监测类型不能为空")
    private Integer monitorType;

    @NotEmpty
    private List<SensorInfluxdbData> dataList;

    @JsonIgnore
    private Integer kind;


    @Override
    public ResultWrapper<?> validate() {

        for (int i = 0; i < dataList.size(); i++) {
            if (CollectionUtil.isNullOrEmpty(dataList.get(i).getSensorDataList())) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "补录数据不能为空");
            }
            for (int j = 0; j < dataList.get(i).getSensorDataList().size(); j++) {
                try {
                    // 尝试转换字符串为 double
                    Double.parseDouble(dataList.get(i).getSensorDataList().get(j).getValue());
                } catch (NumberFormatException e) {
                    return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "补录数据类型必须为double类型");
                }
            }
        }

        if (Objects.equals(monitorType, MonitorType.CHANNEL_WATER_LEVEL.getKey())) {
            kind = 3;
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.companyID.toString(), ResourceType.COMPANY);
    }
}
