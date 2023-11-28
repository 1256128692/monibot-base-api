package cn.shmedo.monitor.monibotbaseapi.model.response.sluice;

import cn.shmedo.monitor.monibotbaseapi.model.enums.sluice.ControlType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Chengfs on 2023/11/21
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Gate extends GateSimple {

    private ControlType controlType;

    private Integer openStatus;

    @JsonIgnore
    private Integer monitorPointID;

    @JsonIgnore
    private Integer monitorType;

    @JsonIgnore
    private String iotSensorName;
}