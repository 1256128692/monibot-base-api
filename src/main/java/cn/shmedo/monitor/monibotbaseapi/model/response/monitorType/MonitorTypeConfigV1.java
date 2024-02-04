package cn.shmedo.monitor.monibotbaseapi.model.response.monitorType;

import lombok.Data;

import java.util.List;

@Data
public class MonitorTypeConfigV1 {

    private List<Integer> displayDensity;

    private List<Integer> statisticalMethods;


}
