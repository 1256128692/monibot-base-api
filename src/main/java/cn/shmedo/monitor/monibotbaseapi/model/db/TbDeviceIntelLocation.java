package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Builder;
import lombok.Data;

/**
 * 智能设备位置表
 */
@Data
@Builder
@TableName(value = "tb_device_intel_location")
public class TbDeviceIntelLocation {
    /**
     * 设备token标识
     */
    @TableField(value = "DeviceToken")
    private String deviceToken;

    /**
     * 类型，0,1对应iot，视频设备
     */
    @TableField(value = "Type")
    private Byte type;

    /**
     * 位置扩展
     */
    @TableField(value = "LocationJson")
    private String locationJson;

    /**
     * 地址
     */
    @TableField(value = "Address")
    private String address;

    @TableField(value = "CreateTime")
    private Date createTime;

    @TableField(value = "CreateUserID")
    private Integer createUserID;

    @TableField(value = "UpdateTime")
    private Date updateTime;

    @TableField(value = "UpdateUserID")
    private Integer updateUserID;
}