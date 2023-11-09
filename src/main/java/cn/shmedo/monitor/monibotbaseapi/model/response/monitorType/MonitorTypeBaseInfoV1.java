package cn.shmedo.monitor.monibotbaseapi.model.response.monitorType;

import lombok.Data;

import java.util.List;

@Data
public class MonitorTypeBaseInfoV1 {

    private Integer monitorType;

    private String typeName;

    private String exValues;

    private List<Integer> displayDensity;

    private List<Integer> statisticalMethods;

}
