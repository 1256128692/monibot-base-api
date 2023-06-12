package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint.MonitorPointWithSensor;
import cn.shmedo.monitor.monibotbaseapi.model.response.project.QueryProjectBaseInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.model.response.sensor.SensorBaseInfoResponse;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class QueryProjectBaseInfoListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {


    @NotNull
    private Integer companyID;

    private String monitorItemName;

    @JsonIgnore
    private Collection<Integer> projectIDs;


    @JsonIgnore
    private List<QueryProjectBaseInfoResponse> projectInfoResponseList;

    @JsonIgnore
    private List<MonitorPointWithSensor> monitorPointWithSensorList;

    @JsonIgnore
    private List<SensorBaseInfoResponse> sensorBaseInfoResponseList;


    @Override
    public ResultWrapper<?> validate() {

        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
        TbSensorMapper tbSensorMapper = ContextHolder.getBean(TbSensorMapper.class);
        // 1.根据有权限的工程ID,查询工程信息列表
        projectInfoResponseList = tbProjectInfoMapper.selectListByCompanyIDAndMonitorItemName(companyID, monitorItemName);

        List<Integer> projectIDList = projectInfoResponseList.stream().map(QueryProjectBaseInfoResponse::getProjectID).collect(Collectors.toList());
        if (CollectionUtil.isNullOrEmpty(projectIDList)) {
            return ResultWrapper.successWithNothing();
        }
        // 2.校验项目权限
        Collection<Integer> permissionProjectList = PermissionUtil.getHavePermissionProjectList(companyID, projectIDList);
        if (permissionProjectList.isEmpty()) {
            return ResultWrapper.withCode(ResultCode.SUCCESS, "");
        } else {
            projectIDs = permissionProjectList.stream().toList();
        }

        // 3.根据有权限的工程ID和监测项目名称,去查询监测点列表
        monitorPointWithSensorList = tbMonitorPointMapper.selectListByProjectIDsAndMonitorItemName(projectIDs.stream().toList(), monitorItemName);

        List<Integer> monitorPointIDList = monitorPointWithSensorList.stream().map(MonitorPointWithSensor::getMonitorPointID).collect(Collectors.toList());
        if (!CollectionUtil.isNullOrEmpty(monitorPointIDList)) {
            // 4.根据监测点ID,去查询传感器列表
            sensorBaseInfoResponseList = tbSensorMapper.selectListBymonitorPointIDList(monitorPointIDList);
        }

        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

}
