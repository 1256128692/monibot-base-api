package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SensorHasDataCountResponse {


    private List<String> dataList;


    private Integer density;
}
