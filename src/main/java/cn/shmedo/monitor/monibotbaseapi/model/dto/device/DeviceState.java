package cn.shmedo.monitor.monibotbaseapi.model.dto.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备状态
 *
 * @author Chengfs on 2023/5/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceState {

    /**
     * 设备状态
     */
    private Integer status;

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
     * 固件版本
     */
    private String swVersion;

    /**
     * 位置
     */
    private String location;

    /**
     * 传感器错误码
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

    /**
     * 剩余电量百分比
     */
    private Double voltPercent;

    /**
     * 设备启动后工作时间，单位秒
     */
    private Integer uptime;

    /**
     * 湿度设备物联网卡iccid，多个逗号分隔
     */
    private String iccid;

    /**
     * 通信模块身份识别码
     */
    private String imei;

    /**
     * 设备平台在线时间百分比
     */
    private Double onlinePercent;

    /**
     * 设备重启代码
     */
    private String rebootCode;

    /**
     * LORA上行信号强度，单位dbm
     */
    private Double loraUpSignal;

    /**
     * LORA下行信号强度，单位dbm
     */
    private Double loraDownSignal;
}