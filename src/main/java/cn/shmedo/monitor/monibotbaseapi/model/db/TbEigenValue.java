package cn.shmedo.monitor.monibotbaseapi.model.db;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbEigenValue implements Serializable {
    /**
    * 主键
    */
    private Integer id;

    /**
    * 工程ID
    */
    private Integer projectID;

    /**
    * 作用范围 0:专题分析 1:历史数据
    */
    private Integer scope;

    /**
    * 监测项目ID
    */
    private Integer monitorItemID;

    /**
    * 监测子类型
    */
    private Integer monitorTypeFieldID;

    /**
    * 特征值名称
    */
    private String name;

    /**
    * 数值
    */
    private Double value;

    /**
    * 单位ID
    */
    private Integer unitID;

    /**
    * 拓展属性
    */
    private String exValue;

    /**
    * 创建用户
    */
    private Integer createUserID;

    /**
    * 创建时间
    */
    private Date createTime;

    /**
    * 修改用户
    */
    private Integer updateUserID;

    /**
    * 修改时间
    */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}