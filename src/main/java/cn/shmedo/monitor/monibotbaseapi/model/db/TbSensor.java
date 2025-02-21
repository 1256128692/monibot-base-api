package cn.shmedo.monitor.monibotbaseapi.model.db;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 传感器表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbSensor implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer ID;

    /**
     * 工程项目ID
     */
    private Integer projectID;

    /**
     * 监测类型模板ID
     */
    private Integer templateID;

    /**
     * 数据源分布式唯一ID
     */
    private String dataSourceID;

    /**
     * 模板数据来源类型
     * 1 -  单一物模型单一传感器
     * 2 -  多个物联网传感器（同一物模型多个或者不同物模型多个）
     * 3 -  物联网传感器+监测传感器
     * 4 - 1个监测传感器
     * 5 - 多个监测传感器
     * 100 - API 推送
     */
    private Integer dataSourceComposeType;

    /**
     * 监测类型
     */
    private Integer monitorType;

    /**
     * 传感器名称
     */
    private String name;

    /**
     * 传感器别名
     */
    private String alias;

    /**
     * 传感器类型　1 - 自动化传感器
     * 2 - 融合传感器
     * 3 - 人工传感器
     */
    private Byte kind;

    /**
     * 显示排序字段
     */
    private Integer displayOrder;

    /**
     * 关联监测点ID
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer monitorPointID;

    /**
     * 存储监测类型拓展配置值。
     */
    private String configFieldValue;

    /**
     * 拓展信息
     */
    private String exValues;

    /**
     * 传感器状态 -1 无数据 0 正常 1,2,3,4对应预警级别
     */
    private Byte status;

    /**
     * 设备在线情况,0:离线 1:正常 null:离线
     */
    private Integer OnlineStatus;

    /**
     * 无数据报警是否开启
     */
    private Boolean warnNoData;

    /**
     * 监测数据开始时间
     */
    private Date monitorBeginTime;

    /**
     * 传感器图片
     */
    private String imagePath;

    /**
     * 是否开启，开启则平台接收实体传感器上传的数据，反之不接收
     */
    private Boolean enable;

    /**
     * 创建时间
     */
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Date createTime;

    /**
     * 创建用户ID
     */
    private Integer createUserID;

    /**
     * 修改时间
     */
    @TableField(insertStrategy = FieldStrategy.NEVER)
    private Date updateTime;

    /**
     * 修改用户ID
     */
    private Integer updateUserID;


    /**
     * 视频设备ID
     */
    private Integer videoDeviceID;


    /**
     * 视频设备通道号
     */
    private Integer channelCode;

    private Integer videoDeviceSourceID;

    private static final long serialVersionUID = 1L;
}