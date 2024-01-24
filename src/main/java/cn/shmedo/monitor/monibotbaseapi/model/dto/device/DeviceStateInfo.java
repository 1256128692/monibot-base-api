package cn.shmedo.monitor.monibotbaseapi.model.dto.device;

import lombok.Data;

import java.util.Date;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-23 14:59
 */
@Data
public class DeviceStateInfo {
    /**
     * 设备ID
     */
    private Integer deviceID;
    /**
     * 设备SN
     */
    private String deviceToken;
    /**
     * 状态时间
     */
    private Date time;
    /**
     * 外接电源电压
     */
    private Double extPowerVolt;
    /**
     * 内接电源电压
     */
    private Double innerPowerVolt;
    /**
     * 温度
     */
    private Double temp;
    /**
     * 湿度
     */
    private Double humidity;
    /**
     * 设备外部环境温度
     */
    private Double tempOut;
    /**
     * 设备外部环境湿度
     */
    private Double humidityOut;
    /**
     * 4G信号强度
     */
    private Double fourGSignal;
    /**
     * 北斗功率等级
     */
    private Double bdSignal;
    /**
     * NB信号强度
     */
    private Double nbSignal;
    /**
     * 固件版本
     */
    private String swVersion;
    /**
     * 位置
     */
    private String location;
    /**
     * 传感器状态码
     */
    private String sensorErrno;
    /**
     * 太阳能板电压
     */
    private Double solarVolt;
    /**
     * 蓄电池电压
     */
    private Double batteryVolt;
    /**
     * 充电功率
     */
    private Double supplyPower;
    /**
     * 消耗功率
     */
    private Double consumePower;
    /**
     * 设备工作电流
     */
    private Double workCurrent;
}
