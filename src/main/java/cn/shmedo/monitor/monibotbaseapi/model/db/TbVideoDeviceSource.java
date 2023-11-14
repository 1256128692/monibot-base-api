package cn.shmedo.monitor.monibotbaseapi.model.db;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbVideoDeviceSource implements Serializable {
    /**
    * 主键
    */
    private Integer ID;

    /**
    * 公司ID
    */
    private Integer companyID;

    /**
    * 接入平台 0-萤石云平台 1-海康平台
    */
    private Byte accessPlatform;

    /**
    * 视频设备序列号
    */
    private String deviceSerial;

    /**
    * 视频通道号
    */
    private Integer channelNo;

    /**
    * 物联网平台设备UniqueToken
    */
    private String iotUniqueToken;

    /**
    * 是否启用
    */
    private Boolean enable;

    /**
    * 创建时间
    */
    private Date createTime;

    /**
    * 创建用户ID
    */
    private Integer createUserID;

    /**
    * 关闭时间
    */
    private Date closeTime;

    private static final long serialVersionUID = 1L;
}