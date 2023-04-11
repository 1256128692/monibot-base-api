package cn.shmedo.monitor.monibotbaseapi.model.db;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
    * 监测项目表
    */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TbMonitorItem implements Serializable {
    /**
    * 主键
    */
    private Integer ID;

    /**
    * 公司ID
    */
    private Integer companyID;


    /**
     * 监测类型
     */
    private Integer projectID;

    private Integer monitorClass;

    /**
    * 监测类型
    */
    private Integer monitorType;

    /**
    * 监测项目名称
    */
    private String name;

    /**
    * 监测项目别名
    */
    private String alias;

    /**
    * 创建类型
    */
    private Byte createType;

    /**
    * 项目类型(行业信息)
    */
    private Integer projectType;

    /**
    * 拓展字段
    */
    private String exValue;

    /**
    * 显示排序
    */
    private Integer displayOrder;

    /**
    * 创建时间
    */
    private Date createTime;

    /**
    * 创建用户ID
    */
    private Integer createUserID;

    /**
    * 修改时间
    */
    private Date updateTime;

    /**
    * 修改用户ID
    */
    private Integer updateUserID;

    private static final long serialVersionUID = 1L;
}