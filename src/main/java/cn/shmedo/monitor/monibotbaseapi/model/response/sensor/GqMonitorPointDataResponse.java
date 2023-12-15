package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpointdata.FieldBaseInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GqMonitorPointDataResponse {

    private List<SensorBaseInfoV3> sensorInfoList;

    private List<FieldBaseInfo> fieldInfoList;

}
