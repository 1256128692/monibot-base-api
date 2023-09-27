package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

/**
 * 其他设备表，非iot，非视频
 */
@Data
@Builder
@TableName(value = "tb_other_device")
public class TbOtherDevice {
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer ID;

    @TableField(value = "CompanyID")
    private Integer companyID;

    @TableField(value = "ProjectID")
    private Integer projectID;

    @TableField(value = "`Name`")
    private String name;

    /**
     * 标识
     */
    @TableField(value = "Token")
    private String token;

    /**
     * 型号
     */
    @TableField(value = "Model")
    private String model;

    /**
     * 厂商或品牌
     */
    @TableField(value = "Vendor")
    private String vendor;

    /**
     * 模板ID
     */
    @TableField(value = "TemplateID")
    private Integer templateID;

    @TableField(value = "ExValue")
    private String exValue;

    @TableField(value = "CreateTime")
    private Date createTime;

    @TableField(value = "CreateUserID")
    private Integer createUserID;

    @TableField(value = "UpdateTime")
    private Date updateTime;

    @TableField(value = "UpdateUserID")
    private Integer updateUserID;
}