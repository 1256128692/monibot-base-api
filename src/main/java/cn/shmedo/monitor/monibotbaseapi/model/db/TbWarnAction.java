package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.util.Date;

@Data
@TableName("tb_warn_action")
public class TbWarnAction {
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer ID;
    @TableField("triggerID")
    private Integer triggerID;
    @TableField("ActionType")
    private Integer actionType;
    @TableField("ActionTarget")
    private String actionTarget;
    @TableField("Desc")
    private String desc;
    @Serial
    private static final long serialVersionUID = 1L;
}
