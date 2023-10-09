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
 * 属性模板分组表
 * @author wuxl
 * @TableName tb_property_model_group
 */
@TableName(value ="tb_property_model_group")
@Data
public class TbPropertyModelGroup implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer ID;

    /**
     * 公司ID
     */
    @TableField(value = "CompanyID")
    private Integer companyID;

    /**
     * 所属平台
     */
    @TableField(value = "Platform")
    private String platform;

    /**
     * 模板组类型（0-工程项目（默认值）；1-设备；2-工作流）
     */
    @TableField(value = "GroupType")
    private Integer groupType;

    /**
     * 模板组类型子分类（冗余字段，当且仅当ModeyType为2-工作流时候，有值）
     */
    @TableField(value = "GroupTypeSubType")
    private Integer groupTypeSubType;

    /**
     * 名称
     */
    @TableField(value = "`Name`")
    private String name;

    /**
     * 描述
     */
    @TableField(value = "`Desc`")
    private String desc;

    /**
     * 扩展字段
     */
    @TableField(value = "ExValue")
    private String exValue;

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