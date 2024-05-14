package cn.shmedo.monitor.monibotbaseapi.model.db;

import cn.shmedo.monitor.monibotbaseapi.model.enums.ScopeType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tb_eigen_value")
public class TbEigenValue implements Serializable {
    /**
    * 主键
    */
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    /**
    * 工程ID
    */
    @TableField("ProjectID")
    private Integer projectID;

    /**
    * 作用范围 0:专题分析 1:历史数据
    */
    @TableField("Scope")
    private ScopeType scope;

    /**
    * 监测项目ID
    */
    @TableField("MonitorItemID")
    private Integer monitorItemID;

    /**
    * 监测子类型
    */
    @TableField("MonitorTypeFieldID")
    private Integer monitorTypeFieldID;

    /**
    * 特征值名称
    */
    @TableField("Name")
    private String name;

    /**
    * 数值
    */
    @TableField("Value")
    private Double value;

    /**
    * 单位ID
    */
    @TableField("UnitID")
    private Integer unitID;

    /**
     * 关联所有监测点
     */
    @TableField(value = "AllMonitorPoint")
    private Boolean allMonitorPoint;

    /**
    * 拓展属性
    */
    @TableField("ExValue")
    private String exValue;

    /**
    * 创建用户
    */
    @TableField("CreateUserID")
    private Integer createUserID;

    /**
    * 创建时间
    */
    @TableField("CreateTime")
    private Date createTime;

    /**
    * 修改用户
    */
    @TableField("UpdateUserID")
    private Integer updateUserID;

    /**
    * 修改时间
    */
    @TableField("UpdateTime")
    private Date updateTime;

    @Serial
    private static final long serialVersionUID = 1L;
}