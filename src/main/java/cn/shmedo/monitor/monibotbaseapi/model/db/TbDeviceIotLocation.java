package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 物联网设备位置表
 */
@Data
@Builder
@TableName(value = "tb_device_iot_location")
public class TbDeviceIotLocation {
    /**
     * 设备token
     */
    @TableId(value = "DeviceToken", type = IdType.INPUT)
    private String deviceToken;

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