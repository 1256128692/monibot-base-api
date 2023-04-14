package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;

@Data
@Accessors(chain = true)
@TableName("tb_warn_action")
public class TbWarnAction {
    /**
     * 这个类会被用作入参的一部分,jackson在设置内层json时会将ID转成id,所以需要加别称
     * 别称的处理
     *
     * @see com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap#_find2(String, int, Object)
     */
    @SuppressWarnings("JavadocReference")
    @JsonProperty("ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer ID;
    @TableField("triggerID")
    private Integer triggerID;
    @TableField("ActionType")
    private Integer actionType;
    @TableField("ActionTarget")
    private String actionTarget;
    @TableField("`Desc`")
    private String desc;
    @Serial
    private static final long serialVersionUID = 1L;
}
