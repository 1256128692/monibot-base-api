package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
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
     * 项目类型ID
     */
    private Byte projectTypeID;

    /**
     * 监测点ID
     */
    private Integer monitorPointID;


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
     * 项目(工程)短名称
     */
    private String projectShortName;

    /**
     * 项目(类型)名称
     */
    private String projectTypeName;


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
    private Integer monitorItemID;

    /**
     * 监测项名称
     */
    private String monitorItemName;

    /**
     * 监测项别名
     */
    private String monitorItemAlias;

    /**
     * 四级行政区域信息
     */
    private String location;


    /**
     * 第四级区域名称
     */
    private String locationInfo;

    /**
     * 监测点地图经纬度
     */
    private String gpsLocation;


    /**
     * 监测点底图位置
     */
    private String imageLocation;

    /**
     * 传感器信息
     */
    private List<TbSensor> sensorList;

    /**
     * 是否包含多传感器
     */
    private Boolean multiSensor;


    /**
     * 单个传感器的数据信息
     */
    private Map<String, Object> sensorData;


    /**
     * 批量传感器的数据信息
     */
    private List<Map<String, Object>> multiSensorData;

    /**
     * 采集时间
     */
    private Date time;

    /**
     * 监测类型属性字段列表
     */
    private List<FieldSelectInfo> fieldList;

    /**
     * 监测类型属性字段单位列表
     */
    private List<TbDataUnit> dataUnitList;


    public static SensorNewDataInfo reBuildProAndMonitor(TbMonitorPoint tbMonitorPoint, TbProjectInfo tbProjectInfo,
                                                         Map<Byte, TbProjectType> projectTypeMap, List<TbSensor> sensorList,
                                                         Map<Integer, TbMonitorType> monitorTypeMap, List<TbMonitorItem> TbMonitorItems) {
        SensorNewDataInfo vo = new SensorNewDataInfo();
        vo.setProjectID(tbProjectInfo.getID());
        vo.setProjectTypeID(tbProjectInfo.getProjectType());
        vo.setMonitorPointID(tbMonitorPoint.getID());
        vo.setMonitorType(tbMonitorPoint.getMonitorType());
        vo.setProjectName(tbProjectInfo.getProjectName());
        vo.setProjectShortName(tbProjectInfo.getShortName());
        // 缓存
        vo.setProjectTypeName(projectTypeMap.get(tbProjectInfo.getProjectType()).getTypeName());
        vo.setMonitorPointName(tbMonitorPoint.getName());
        // 缓存
        vo.setMonitorTypeName(monitorTypeMap.get(tbMonitorPoint.getMonitorType()).getTypeName());
        vo.setLocation(tbProjectInfo.getLocation());
        if (StringUtils.isNotBlank(tbProjectInfo.getLocation())){
            if (JSONUtil.isTypeJSON(tbProjectInfo.getLocation())) {
                JSONObject json = JSONUtil.parseObj(tbProjectInfo.getLocation());
                vo.setLocationInfo(json.isEmpty() ? null : CollUtil.getLast(json.values()).toString());
            }
        }

        vo.setGpsLocation(tbMonitorPoint.getGpsLocation());
        vo.setImageLocation(tbMonitorPoint.getImageLocation());
        vo.setMonitorItemID(tbMonitorPoint.getMonitorItemID());
        vo.setMonitorItemName(TbMonitorItems.stream().filter(tmi -> tmi.getID().equals(tbMonitorPoint.getMonitorItemID())).findFirst().get().getName());
        vo.setMonitorItemAlias(TbMonitorItems.stream().filter(tmi -> tmi.getID().equals(tbMonitorPoint.getMonitorItemID())).findFirst().get().getAlias());
        vo.setSensorList(sensorList);
        if (!CollectionUtil.isNullOrEmpty(sensorList)){
            if (sensorList.size() > 1){
                vo.setMultiSensor(true);
            }else {
                vo.setMultiSensor(false);
            }
        }
        return vo;
    }
}
