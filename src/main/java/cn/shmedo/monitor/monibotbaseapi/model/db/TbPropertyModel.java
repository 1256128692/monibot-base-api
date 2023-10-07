package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 项目模板关系表
 * @author wuxl
 * @TableName tb_property_model
 */
@TableName(value ="tb_property_model")
@Data
public class TbPropertyModel implements Serializable {
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
     * 模板名称
     */
    @TableField(value = "Name")
    private String name;

    /**
     * 所属平台
     */
    @TableField(value = "Platform")
    private String platform;

    /**
     * 模板类型（0-工程项目；1-设备；2-工作流）
     */
    @TableField(value = "ModelType")
    private Integer modelType;

    /**
     * 模板类型子分类（冗余字段，当且仅当ModeyType为2-工作流时候，有值）
     */
    @TableField(value = "ModelTypeSubType")
    private Integer modelTypeSubType;

    /**
     * 模板组ID
     */
    @TableField(value = "GroupID")
    private Integer groupID;

    /**
     * 创建类型 0预定义 1自定义
     */
    @TableField(value = "CreateType")
    private Integer createType;

    /**
     * 模板描述
     */
    @TableField(value = "`Desc`")
    private String desc;

    /**
     * 创建时间
     */
    @TableField(value = "CreateTime")
    private LocalDateTime createTime;

    /**
     * 创建人ID
     */
    @TableField(value = "CreateUserID")
    private Integer createUserID;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}