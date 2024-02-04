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
 * @date 2024-01-11 14:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_trigger_config")
public class TbTriggerConfig {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    /**
     * 公司ID
     */
    @TableField("ProjectID")
    private Integer projectID;
    /**
     * 平台
     */
    @TableField("Platform")
    private Integer platform;
    /**
     * 监测项目
     */
    @TableField("MonitorItemID")
    private Integer monitorItemID;
    /**
     * 满足规则的次数
     */
    @TableField("TriggerTimes")
    private Integer triggerTimes;
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
