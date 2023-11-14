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
    /**
     * 比较区间json<br>
     * 上行数据值在'比较值'上还是下报警是根据需求在{@code CompareInterval}枚举写死的,因为前端生成json时并不知道是哪种情况下报警。
     * <p>
     * define:<br>
     * upperName-string-比较区间名称;<br>
     * upperLimit-double-比较值;<br>
     * unit-string-单位;<br>
     * limitType-int-是否包含端点(0.不包含 1.包含).
     * </p>
     * <p>
     * example: {"upperName":"暴雨","upperLimit":"50","unit":"mm","limitType":1}
     * </p>
     */
    @TableField("CompareRule")
    private String compareRule;
    /**
     * 触发条件json
     * <p>
     * define:<br>
     * type-int-触发规则类型(1.连续满足,连续xx次满足条件触发报警 2.时限满足,xx小时内xx次满足条件触发报警);<br>
     * times-int-次数;<br>
     * timeLimit-long-时间限制(单位:秒).
     * </p>
     * <p>
     * example: {"type":1,"times":1,"timeLimit":null}
     * </P>
     */
    @TableField("TriggerRule")
    private String triggerRule;
    @Serial
    private static final long serialVersionUID = 1L;
}
