package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-11 15:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_warn_notify_relation")
public class TbWarnNotifyRelation {
    /**
     * 主键
     */
    @TableField("ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    /**
     * 类型 1.数据报警 2.设备报警
     */
    @TableField("Type")
    private Integer type;
    /**
     * 报警记录ID
     */
    @TableField("WarnLogID")
    private Integer warnLogID;
    /**
     * 通知记录ID
     */
    @TableField("NotifyID")
    private Integer notifyID;
}
