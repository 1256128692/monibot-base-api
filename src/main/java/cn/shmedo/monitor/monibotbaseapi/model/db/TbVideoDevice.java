package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-31 13:45
 */
@Data
@TableName("tb_video_device")
public class TbVideoDevice {
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer ID;
    /**
     * 设备名称
     */
    @TableField("DeviceName")
    private String deviceName;
    /**
     * 设备类型/型号
     */
    @TableField("DeviceType")
    private String deviceType;
    /**
     * 注册公司ID
     */
    @TableField("CompanyID")
    private Integer companyID;
    /**
     * 设备序列号/监测点唯一标识
     */
    @TableField("DeviceSerial")
    private String deviceSerial;
    /**
     * 设备在线状态  true:1 false:0
     */
    @TableField("DeviceStatus")
    private Boolean deviceStatus;
    /**
     * 设备可接入的通道号数量
     */
    @TableField("DeviceChannelNum")
    private Integer deviceChannelNum;
    /**
     * 接入的通道号数量
     */
    @TableField("AccessChannelNum")
    private Integer accessChannelNum;
    /**
     * 平台:萤石云平台:0 海康平台:1
     */
    @TableField("AccessPlatform")
    private Integer accessPlatform;
    /**
     * 协议:萤石云协议:0  国标协议:1
     */
    @TableField("AccessProtocol")
    private Integer accessProtocol;
    /**
     * 项目ID
     */
    @TableField("ProjectID")
    private Integer projectID;
    /**
     * 存储类型 本地:0 云端:1 (暂时不用)
     */
    @TableField("StorageType")
    private Integer storageType;
    /**
     * 设备配置抓拍 true:1 false:0
     */
    @TableField("CaptureStatus")
    private Boolean captureStatus;
    /**
     * 设备分配状态 true:1 false:0
     */
    @TableField("AllocationStatus")
    private Boolean allocationStatus;
    /**
     * 创建用户ID
     */
    @TableField("CreateUserID")
    private Integer createUserID;
    /**
     * 创建时间
     */
    @TableField("CreateTime")
    private Date createTime;
    /**
     * 最后更新用户ID
     */
    @TableField("UpdateUserID")
    private Integer updateUserID;
    /**
     * 最后更新时间
     */
    @TableField("UpdateTime")
    private Date updateTime;
    @TableField("ExValue")
    private String exValue;
    @TableField("S1")
    private String s1;
    @TableField("S2")
    private String s2;
}
