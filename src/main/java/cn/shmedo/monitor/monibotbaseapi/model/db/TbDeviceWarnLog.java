package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 14:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_device_warn_log")
public class TbDeviceWarnLog {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    /**
     * 公司ID
     */
    @TableField("CompanyID")
    private Integer companyID;
    /**
     * 所属平台
     */
    @TableField("Platform")
    private Integer platform;

    @TableField("ProjectID")
    private Integer projectID;

    /**
     * 设备序列号
     */
    @TableField("DeviceSerial")
    private String deviceSerial;
    /**
     * 设备Token
     */
    @TableField("DeviceToken")
    private String deviceToken;
    /**
     * 报警时间
     */
    @TableField("WarnTime")
    private Date warnTime;
    /**
     * 报警结束时间
     */
    @TableField("WarnEndTime")
    private Date warnEndTime;
    /**
     * 报警内容
     */
    @TableField("WarnContent")
    private String warnContent;
    /**
     * 数据状态 0:正常 1:异常
     */
    @TableField("DataStatus")
    private Integer dataStatus;
    /**
     * 处理状态 0:未处理 1:已处理
     */
    @TableField("DealStatus")
    private Integer dealStatus;
    /**
     * 处理类型  1.派发工单 2.填写处理意见
     */
    @TableField("DealType")
    private Integer dealType;
    /**
     * 处理时间
     */
    @TableField("DealTime")
    private Date dealTime;
    /**
     * 处理意见
     */
    @TableField("DealContent")
    private String dealContent;
    /**
     * 工单ID
     */
    @TableField("WorkOrderID")
    private Integer workOrderID;
    /**
     * 拓展字段
     */
    @TableField("ExValue")
    private String exValue;
    /**
     * 处理人ID
     */
    @TableField("DealUserID")
    private Integer dealUserID;
}
