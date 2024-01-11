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
 * @date 2024-01-11 14:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_data_warn_log_history")
public class TbDataWarnLogHistory {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    /**
     * 报警记录ID
     */
    @TableField("WarnLogID")
    private Integer warnLogID;
    /**
     * 报警时间
     */
    @TableField("WarnTime")
    private Date warnTime;
    /**
     * 报警等级
     */
    @TableField("WarnLevel")
    private Integer warnLevel;
    /**
     * 拓展字段
     */
    @TableField("ExValue")
    private String exValue;
}
