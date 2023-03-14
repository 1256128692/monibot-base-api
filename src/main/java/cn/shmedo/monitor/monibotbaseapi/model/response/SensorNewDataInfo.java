package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.shmedo.monitor.monibotbaseapi.model.db.RegionArea;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author rjx
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SensorNewDataInfo {
    /**
     * 项目ID
     */
    private Integer projectID;

    /**
     * 监测点ID
     */
    private Integer monitorID;


    /**
     * 监测类型ID
     * 小于10000是预定义预留
     * 整个系统内不重复
     */
    private Integer monitorType;

    /**
     * 项目(工程)名称
     */
    private String projectName;


    /**
     * 监测点名称
     */
    private String monitorPointName;

    /**
     * 监测类型名称
     */
    private String monitorTypeName;

    /**
     * 监测项ID
     */
    private String monitorItemID;

    /**
     * 监测项名称
     */
    private String monitorItemName;

    /**
     * 监测项别名
     */
    private String monitorItemAlias;


    /**
     * 行政区划
     */
    //
    private RegionArea regionArea;


    /**
     * 传感器的key:value
     */
    private List<Map<String, Object>> sensorData;

    /**
     * 采集时间
     */
    private Date time;




}
