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
 * @date 2024-01-11 15:08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_warn_notify_config")
public class TbWarnNotifyConfig {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;
    /**
     * 公司ID
     */
    @TableField("CompanyID")
    private Integer companyID;
    /**
     * 平台
     */
    @TableField("Platform")
    private Integer platform;
    /**
     * 1.设备报警通知 2.数据报警通知
     */
    @TableField("NotifyType")
    private Integer notifyType;
    /**
     * 报警等级多选,仅NotifyType为 2.数据报警通知 时会配置该项
     */
    @TableField("WarnLevel")
    private String warnLevel;
    /**
     * 通知方式(多选), 1.平台消息 2.短信 3.邮件
     */
    @TableField("NotifyMethod")
    private String notifyMethod;
    /**
     * 选中的部门(多选),形式:[部门id,部门id,...]
     */
    @TableField("Depts")
    private String depts;
    /**
     * 选中的用户(多选),其中包含外部联系人用户,形式:[用户id,用户id,...]
     */
    @TableField("Users")
    private String users;
    /**
     * 选中的角色(多选),形式:[角色id,角色id,...]
     */
    @TableField("Roles")
    private String roles;
    /**
     * 扩展信息,可能包含对某个用户id指定特殊电话的映射
     */
    @TableField("ExValue")
    private String exValue;
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
