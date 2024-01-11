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
 * @date 2024-01-11 14:47
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_notify_config_project_relation")
public class TbNotifyConfigProjectRelation {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    /**
     * 工程ID,为-1时表示关联当前公司下的全部工程
     */
    @TableField("ProjectID")
    private Integer projectID;
    /**
     * 通知配置ID
     */
    @TableField("NotifyConfigID")
    private Integer notifyConfigID;
}
