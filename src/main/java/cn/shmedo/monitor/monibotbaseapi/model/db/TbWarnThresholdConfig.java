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
 * @date 2024-01-11 15:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_warn_threshold_config")
public class TbWarnThresholdConfig {
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
     * 工程ID
     */
    @TableField("ProjectID")
    private Integer projectID;
    /**
     * 监测类型
     */
    @TableField("MonitorType")
    private Integer monitorType;
    /**
     * 监测项目ID
     */
    @TableField("MonitorItemID")
    private Integer monitorItemID;
    /**
     * 比较方式 1.在区间内 2.偏离区间 3.大于 4.大于等于 5.小于 6.小于等于
     */
    @TableField("CompareMode")
    private Integer compareMode;
    /**
     * 传感器ID
     */
    @TableField("SensorID")
    private Integer sensorID;
    /**
     * 子属性ID
     */
    @TableField("FieldID")
    private Integer fieldID;
    /**
     * 是否启用,0.未启用 1.启用(默认)
     */
    @TableField("`Enable`")
    private Boolean enable;
    /**
     * 报警名称
     */
    @TableField("WarnName")
    private String warnName;
    /**
     * 报警等级阈值配置json,形如{"1":{"upper":100,"lower":50},"2":{"upper":50,"lower":25}},其中key为报警等级枚举,枚举: 1:1级,2:2级,3:3级,4:4级;如果比较方式为区间,则value里有upper和lower两个值,否则只有一个upper值
     */
    @TableField("`Value`")
    private String value;
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
    /**
     * 拓展字段
     */
    @TableField("ExValue")
    private String exValue;
}
