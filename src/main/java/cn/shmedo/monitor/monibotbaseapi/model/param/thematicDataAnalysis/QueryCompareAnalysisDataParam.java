package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.iot.entity.api.monitor.enums.DataSourceComposeType;
import cn.shmedo.iot.entity.api.monitor.enums.FieldClass;
import cn.shmedo.iot.entity.api.monitor.enums.FieldDataType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorDataSourceMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensorDataSource;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.MonitorTypeFieldV2;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import org.hibernate.validator.constraints.Range;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-22 15:23
 */
@Data
public class QueryCompareAnalysisDataParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "工程ID不能为空")
    @Positive(message = "工程ID不能小于1")
    private Integer projectID;
    @NotEmpty(message = "属性标识不能为空")
    private String fieldToken;
    @NotNull(message = "开始时间不能为空")
    private Timestamp startTime;
    @NotNull(message = "结束时间不能为空")
    private Timestamp endTime;
    @NotNull(message = "自动传感器ID不能为空")
    @Positive(message = "自动传感器ID不能小于1")
    private Integer autoSensorID;
    @NotNull(message = "手动传感器ID不能为空")
    @Positive(message = "手动传感器ID不能小于1")
    private Integer manualSensorID;
    @NotNull(message = "分析间隔类型不能为空")
    @Range(min = 1, max = 2, message = "分析间隔类型 1.分钟 2.小时")
    private Integer intervalType;
    @NotNull(message = "分析间隔值不能为空")
    @Positive(message = "分析间隔值不能不能小于0")
    private Double intervalValue;
    @JsonIgnore
    private Integer monitorType;
    @JsonIgnore
    private FieldDataType fieldDataType;
    @JsonIgnore
    private MonitorTypeFieldV2 monitorTypeField;
    @JsonIgnore
    private List<Integer> sensorIDList = List.of(autoSensorID, manualSensorID);
    @JsonIgnore
    private Long interval = (long) (60000 * (intervalType == 1 ? intervalType : intervalValue * 60));
    @JsonIgnore
    private List<FieldSelectInfo> fieldSelectInfoList;

    @Override
    public ResultWrapper<?> validate() {
        List<TbSensor> sensorList = ContextHolder.getBean(TbSensorMapper.class).selectList(new LambdaQueryWrapper<TbSensor>()
                .eq(TbSensor::getProjectID, projectID).in(TbSensor::getID, sensorIDList));
        if (sensorList.size() != 2) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有传感器不存在!");
        }
        // 校验自动传感器ID是否是自动传感器的ID、手动传感器ID是否是手动传感器的ID
        Map<String, Integer> dataSourceIDSensorIDMap = sensorList.stream().collect(Collectors.toMap(TbSensor::getDataSourceID, TbSensor::getID, (o1, o2) -> o1));
        List<TbSensorDataSource> tbSensorDataSourceList = ContextHolder.getBean(TbSensorDataSourceMapper.class).selectList(new LambdaQueryWrapper<TbSensorDataSource>()
                .in(TbSensorDataSource::getDataSourceID, sensorList.stream().map(TbSensor::getDataSourceID).distinct().toList()));
        if (tbSensorDataSourceList.size() != 2) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "传感器数据源数据不存在!");
        }
        Map<Integer, DataSourceComposeType> sensorIDComposeTypeMap = tbSensorDataSourceList
                .stream().filter(w -> dataSourceIDSensorIDMap.containsKey(w.getDataSourceID())).collect(Collectors.toMap(
                        k -> dataSourceIDSensorIDMap.get(k.getDataSourceID()),
                        v -> DataSourceComposeType.codeOf(v.getDataSourceComposeType())));
        if (DataSourceComposeType.MANUAL_MONITOR_DATA.equals(sensorIDComposeTypeMap.get(autoSensorID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "选中的自动传感器是手动传感器!");
        }
        if (!DataSourceComposeType.MANUAL_MONITOR_DATA.equals(sensorIDComposeTypeMap.get(manualSensorID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "选中的手动传感器是自动传感器!");
        }

        List<Integer> monitorTypeList = sensorList.stream().map(TbSensor::getMonitorType).distinct().toList();
        if (monitorTypeList.size() > 1) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "自动传感器与手动传感器的监测类型不同!");
        }
        monitorType = monitorTypeList.get(0);
        List<MonitorTypeFieldV2> monitorTypeFieldV2List = ContextHolder.getBean(TbMonitorTypeFieldMapper.class)
                .queryByMonitorTypesV2(monitorTypeList, false);
        if (monitorTypeFieldV2List.stream().noneMatch(u -> u.getFieldToken().equals(fieldToken))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该监测类型没有这种标识的子属性!");
        }
        monitorTypeField = monitorTypeFieldV2List.stream().filter(u -> u.getFieldToken().equals(fieldToken)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unreachable Exception"));
        fieldDataType = FieldDataType.valueOfString(monitorTypeField.getFieldDataType());
        if (!(FieldDataType.LONG.equals(fieldDataType) || FieldDataType.DOUBLE.equals(fieldDataType))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "非数值型数据不允许参加数据比测!");
        }
        fieldSelectInfoList = monitorTypeFieldV2List.stream().map(u -> {
            FieldSelectInfo info = new FieldSelectInfo();
            info.setFieldToken(u.getFieldToken());
            return info;
        }).toList();
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
