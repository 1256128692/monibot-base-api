package cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroup;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ProjectGroupType;
import cn.shmedo.monitor.monibotbaseapi.util.projectConfig.ProjectConfigKeyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-12 18:26
 */
@Slf4j
@Data
public class SetProjectConfigParam implements IConfigParam, IConfigID, ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "项目ID不能为空")
    @Min(value = 1, message = "项目ID不能小于1")
    private Integer projectID;
    @Min(value = 1, message = "配置ID不能小于1")
    private Integer configID;
    @NotEmpty(message = "要配置的key不能为空")
    private String key;
    @NotEmpty(message = "要配置的group不能为空")
    private String group;
    @NotEmpty(message = "要配置的value不能为空")
    private String value;
    private Integer monitorGroupID;
    private Integer monitorPointID;
    @JsonIgnore
    private TbProjectInfo tbProjectInfo;
    @JsonIgnore
    private TbMonitorGroup tbMonitorGroup;
    @JsonIgnore
    private TbMonitorPoint tbMonitorPoint;
    @JsonIgnore
    private ProjectGroupType projectGroupType;

    @Override
    public ResultWrapper validate() {
        projectGroupType = ProjectGroupType.getProjectGroupType(this);
        TbProjectInfoMapper projectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        List<TbProjectInfo> infos = projectInfoMapper.selectList(new LambdaQueryWrapper<TbProjectInfo>()
                .eq(TbProjectInfo::getID, projectID));
        if (CollectionUtil.isEmpty(infos)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
        }
        tbProjectInfo = infos.get(0);
        if (Objects.nonNull(monitorGroupID)) {
            TbMonitorGroupMapper groupMapper = ContextHolder.getBean(TbMonitorGroupMapper.class);
            List<TbMonitorGroup> tbMonitorGroups = groupMapper.selectList(new LambdaQueryWrapper<TbMonitorGroup>()
                    .eq(TbMonitorGroup::getID, monitorGroupID));
            if (CollectionUtil.isEmpty(tbMonitorGroups)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "要额外配置的监测点分组不存在");
            }
            tbMonitorGroup = tbMonitorGroups.get(0);
        } else if (Objects.nonNull(monitorPointID)) {
            TbMonitorPointMapper pointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
            List<TbMonitorPoint> tbMonitorPoints = pointMapper.selectList(new LambdaQueryWrapper<TbMonitorPoint>()
                    .eq(TbMonitorPoint::getID, monitorPointID));
            if (CollectionUtil.isEmpty(tbMonitorPoints)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "要额外配置的监测点不存在");
            }
            tbMonitorPoint = tbMonitorPoints.get(0);
        }
        switch (projectGroupType) {
            case PROJECT -> ProjectConfigKeyUtils.setKey(this, projectID, true);
            case MONITOR_GROUP -> ProjectConfigKeyUtils.setKey(this, monitorGroupID, false);
            case MONITOR_POINT -> ProjectConfigKeyUtils.setKey(this, monitorPointID, false);
            default -> log.info("未设置对应的分级枚举，将不会拼接key");
        }
        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}
