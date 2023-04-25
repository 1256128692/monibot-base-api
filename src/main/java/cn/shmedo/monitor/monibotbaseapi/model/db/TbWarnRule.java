package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.util.Date;

@Data
@TableName("tb_warn_rule")
public class TbWarnRule {
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer ID;
    @TableField("RuleType")
    private Integer ruleType;
    @TableField("ProjectID")
    private Integer projectID;
    @TableField("MonitorType")
    private Integer monitorType;
    @TableField("MonitorItemID")
    private Integer monitorItemID;
    @TableField("MonitorPointID")
    private Integer monitorPointID;
    @TableField("SensorID")
    private Integer sensorID;
    @TableField("CreateUserID")
    private Integer createUserID;
    @TableField("UpdateUserID")
    private Integer updateUserID;
    @TableField("`Name`")
    private String name;
    @TableField("`Desc`")
    private String desc;
    @TableField("CreateTime")
    private Date createTime;
    @TableField("UpdateTime")
    private Date updateTime;
    @TableField("Enable")
    private Boolean enable;
    @Serial
    private static final long serialVersionUID = 1L;
}
