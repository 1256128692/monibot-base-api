package cn.shmedo.monitor.monibotbaseapi.model.response.sluice;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Chengfs on 2023/11/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GateInfo extends Gate {

    private Double openDegree;
    private Double powerCurrent;
    private Double powerVoltage;
    private Integer runningState;
    private Double maxOpenDegree;
    private Integer limitSwSta;
}