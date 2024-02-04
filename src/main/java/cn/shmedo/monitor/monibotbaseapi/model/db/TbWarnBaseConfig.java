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
 * @date 2024-01-11 14:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_warn_base_config")
public class TbWarnBaseConfig {
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
     * 报警标签枚举 1.报警 2.告警 3.预警
     */
    @TableField("WarnTag")
    private Integer warnTag;
    /**
     * 报警等级类型枚举 1:4级 2:3级
     */
    @TableField("WarnLevelType")
    private Integer warnLevelType;
    /**
     * 等级样式枚举 1:红色,橙色,黄色,蓝色 2:1级,2级,3级,4级 3:Ⅰ级,Ⅱ级,Ⅲ级,Ⅳ级
     */
    @TableField("WarnLevelStyle")
    private Integer warnLevelStyle;
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
