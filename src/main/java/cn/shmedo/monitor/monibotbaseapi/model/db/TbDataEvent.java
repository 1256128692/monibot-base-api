package cn.shmedo.monitor.monibotbaseapi.model.db;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbDataEvent implements Serializable {
    /**
    * 主键
    */
    private Integer id;

    /**
    * 工程ID
    */
    private Integer projectID;

    /**
    * 大事记名称
    */
    private String name;

    /**
    * 频率 0:单次  1:每年
    */
    private Integer frequency;

    /**
    * 时间范围
    */
    private String timeRange;

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
    * 修改用户
    */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}