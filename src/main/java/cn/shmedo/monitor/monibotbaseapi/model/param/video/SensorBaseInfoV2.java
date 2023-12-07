package cn.shmedo.monitor.monibotbaseapi.model.param.video;

import lombok.Data;

import java.util.List;

@Data
public class SensorBaseInfoV2 {

    private Integer monitorType;

    private List<Integer> sensorIDList;

}
