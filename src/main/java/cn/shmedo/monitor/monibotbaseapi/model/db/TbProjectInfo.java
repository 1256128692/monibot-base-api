package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 项目信息主表
 */
@Data
@Builder
@TableName(value = "tb_project_info")
@NoArgsConstructor
@AllArgsConstructor
public class TbProjectInfo {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer ID;

    /**
     * 公司ID
     */
    @TableField(value = "CompanyID")
    private Integer companyID;

    /**
     * -1,0,1,2代表子工程，未分配得非子工程，一级工程，二级工程
     */
    @TableField(value = "`Level`")
    private Byte level;

    /**
     * 项目名称
     */
    @TableField(value = "ProjectName")
    private String projectName;

    /**
     * 项目简称
     */
    @TableField(value = "ShortName")
    private String shortName;

    /**
     * 项目类型
     */
    @TableField(value = "ProjectType")
    private Byte projectType;

    /**
     * 项目有效期
     */
    @TableField(value = "ExpiryDate")
    private Date expiryDate;

    /**
     * 直管单位
     */
    @TableField(value = "DirectManageUnit")
    private String directManageUnit;

    /**
     * 平台类型（废弃）
     */
    @TableField(value = "PlatformType")
    private Byte platformType;

    /**
     * 平台类型集
     */
    @TableField(value = "PlatformTypeSet")
    private String platformTypeSet;

    /**
     * 是否可用，需要配合有效期使用
     * 1:启用，0停用
     */
    @TableField(value = "`Enable`")
    private Boolean enable;

    /**
     * 项目位置信息
     */
    @TableField(value = "`Location`")
    private String location;

    /**
     * 项目地址
     */
    @TableField(value = "ProjectAddress")
    private String projectAddress;

    /**
     * 项目经度
     */
    @TableField(value = "Latitude")
    private Double latitude;

    /**
     * 项目纬度
     */
    @TableField(value = "Longitude")
    private Double longitude;

    /**
     * 项目图片地址
     */
    @TableField(value = "ImagePath")
    private String imagePath;

    /**
     * 项目简介
     */
    @TableField(value = "ProjectDesc")
    private String projectDesc;

    /**
     * 模板ID
     */
    @TableField(value = "ModelID")
    private Integer modelID;

    /**
     * 创建时间
     */
    @TableField(value = "CreateTime")
    private Date createTime;

    /**
     * 创建用户ID
     */
    @TableField(value = "CreateUserID")
    private Integer createUserID;

    /**
     * 修改时间
     */
    @TableField(value = "UpdateTime")
    private Date updateTime;

    /**
     * 修改用户ID
     */
    @TableField(value = "UpdateUserID")
    private Integer updateUserID;
}