package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class GqMonitorPointDataPushParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;
    @NotNull(message = "监测类型不能为空")
    private Integer monitorType;

    private List<SensorInfluxdbData> dataList;


    @Override
    public ResultWrapper<?> validate() {
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.companyID.toString(), ResourceType.COMPANY);
    }
}
