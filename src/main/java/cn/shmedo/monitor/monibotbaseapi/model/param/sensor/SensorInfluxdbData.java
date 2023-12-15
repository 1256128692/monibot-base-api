package cn.shmedo.monitor.monibotbaseapi.model.param.sensor;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SensorInfluxdbData {

    private Integer sid;
    private Date time;

    private List<FieldTokenAndValue> sensorDataList;
}
