package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 项目基础信息关系表
 */
@Data
@Builder
@TableName(value = "tb_project_property")
@AllArgsConstructor
@NoArgsConstructor
public class TbProjectProperty {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer ID;

    /**
     * 对象类型，1:工程 2:其他设备 3:工作流
     */
    @TableField(value = "SubjectType")
    private Integer subjectType;

    /**
     * 项目编号
     */
    @TableField(value = "ProjectID")
    private Long projectID;

    /**
     * 属性ID
     */
    @TableField(value = "PropertyID")
    private Integer propertyID;

    /**
     * 属性值，枚举数值为json数组
     */
    @TableField(value = "`Value`")
    private String value;
}