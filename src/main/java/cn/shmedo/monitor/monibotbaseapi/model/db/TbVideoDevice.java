package cn.shmedo.monitor.monibotbaseapi.model.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 视频设备表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbVideoDevice implements Serializable {
    private Integer ID;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备类型/型号
     */
    private String deviceType;

    /**
     * 注册公司ID
     */
    private Integer companyID;

    /**
     * 设备序列号/监测点唯一标识
     */
    private String deviceSerial;

    /**
     * 设备在线状态  true:1 false:0
     */
    private Boolean deviceStatus;

    /**
     * 接入的通道号数量
     */
    private Integer accessChannelNum;

    /**
     * 平台:萤石云平台:0 海康平台:1
     */
    private Byte accessPlatform;

    /**
     * 协议:萤石云协议:0 , 国标协议:1
     */
    private Byte accessProtocol;

    /**
     * 项目ID
     */
    private Integer projectID;

    /**
     * 存储类型 本地:0 云端:1 (暂时不用)
     */
    private Byte storageType;

    /**
     * 设备配置抓拍 true:1 false:0
     */
    private Boolean captureStatus;

    /**
     * 设备分配状态 true:1 false:0
     */
    private Boolean allocationStatus;

    /**
     * 创建用户ID
     */
    private Integer createUserID;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 最后更新用户ID
     */
    private Integer updateUserID;

    /**
     * 最后更新时间
     */
    private Date updateTime;

    private String exValue;

    private String s1;

    private String s2;

    private static final long serialVersionUID = 1L;
}
