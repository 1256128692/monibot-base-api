package cn.shmedo.monitor.monibotbaseapi.model.response.sluice;

import cn.shmedo.monitor.monibotbaseapi.model.enums.sluice.ControlActionKind;
import cn.shmedo.monitor.monibotbaseapi.model.enums.sluice.ControlActionType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Chengfs on 2023/11/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GateInfo extends Gate {

    //闸门开度
    private Double openDegree;
    //电源电流
    private Double powerCurrent;
    //电源电压
    private Double powerVoltage;
    //电机运行状态 0上、1下、2停
    private Integer runningState;
    //闸门最大开度
    private Double maxOpenDegree;
    //限位开关状态（0：上下限位均未触发；1：上限位触发；2：下限位触发；3：上下均触发）
    private Integer limitSwSta;
    //启闭状态 0：关闭；1：开启
    private Integer gateSta;
    //驱动器温度
    private Double gateTemp;

    private ControlActionKind actionKind;
    private ControlActionType actionType;
}