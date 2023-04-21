package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;

@Data
@TableName("tb_warn_trigger")
public class TbWarnTrigger {
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer ID;
    @TableField("RuleID")
    private Integer ruleID;
    @TableField("WarnLevel")
    private Integer warnLevel;
    @TableField("FieldToken")
    private String fieldToken;
    @TableField("`Name`")
    private String warnName;
    @TableField("CompareRule")
    private String compareRule;
    @TableField("TriggerRule")
    private String triggerRule;
    @Serial
    private static final long serialVersionUID = 1L;
}
