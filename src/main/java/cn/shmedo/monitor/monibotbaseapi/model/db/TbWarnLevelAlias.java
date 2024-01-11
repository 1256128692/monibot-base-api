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
 * @date 2024-01-11 15:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_warn_level_alias")
public class TbWarnLevelAlias {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    /**
     * 报警等级别名
     */
    @TableField("Alias")
    private String alias;
    /**
     * 平台
     */
    @TableField("Platform")
    private Integer platform;
    /**
     * 工程ID
     */
    @TableField("ProjectID")
    private Integer projectID;
    /**
     * 监测类型
     */
    @TableField("MonitorType")
    private Integer monitorType;
    /**
     * 监测项目ID
     */
    @TableField("MonitorItemID")
    private Integer monitorItemID;
    /**
     * 子属性ID
     */
    @TableField("FieldID")
    private Integer fieldID;
    /**
     * 报警等级
     */
    @TableField("WarnLevel")
    private Integer warnLevel;
    /**
     * 创建人ID
     */
    @TableField("CreateUserID")
    private Integer createUserID;
    /**
     * 创建时间
     */
    @TableField("CreateTime")
    private Date createTime;
    /**
     * 修改人ID
     */
    @TableField("UpdateUserID")
    private Integer updateUserID;
    /**
     * 修改时间
     */
    @TableField("UpdateTime")
    private Date updateTime;
}
