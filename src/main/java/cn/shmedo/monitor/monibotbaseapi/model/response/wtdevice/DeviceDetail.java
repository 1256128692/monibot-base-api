package cn.shmedo.monitor.monibotbaseapi.model.response.wtdevice;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbDeviceIotLocation;
import cn.shmedo.monitor.monibotbaseapi.model.dto.device.DeviceState;
import lombok.*;

import java.util.Date;

/**
 * @author Chengfs on 2023/5/17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDetail {

    /**
     * 设备ID
     */
    private Integer deviceID;

    /**
     * 设备SN
     */
    private String deviceToken;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 产品名称（设备型号）
     */
    private String productName;

    /**
     * 固件版本
     */
    private String firmwareVersion;

    /**
     * 在线状态
     */
    private Boolean onlineStatus;

    /**
     * 最后活跃时间
     */
    private Date lastActiveTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 设备状态
     */
    private DeviceState state;

    private TbDeviceIotLocation location;
}