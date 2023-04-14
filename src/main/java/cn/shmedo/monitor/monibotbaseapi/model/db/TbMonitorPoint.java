package cn.shmedo.monitor.monibotbaseapi.model.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 监测点表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TbMonitorPoint {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    private Integer ID;

    /**
     * 工程项目ID
     */
    private Integer projectID;

    /**
     * 监测类型
     */
    private Integer monitorType;

    /**
     * 监测项目ID
     */
    private Integer monitorItemID;

    /**
     * 监测点名称
     */
    private String name;

    /**
     * 安装位置
     */
    private String installLocation;

    /**
     * 地图位置
     */
    private String gpsLocation;

    /**
     * 底图位置
     */
    private String imageLocation;

    /**
     * 全景位置
     */
    private String overallViewLocation;

    /**
     * 三纬位置
     */
    private String spatialLocation;

    /**
     * 是否启用
     * 1:启用，0停用
     */
    private Boolean enable;

    /**
     * 拓展字段
     */
    private String exValues;

    /**
     * 排序字段
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

}