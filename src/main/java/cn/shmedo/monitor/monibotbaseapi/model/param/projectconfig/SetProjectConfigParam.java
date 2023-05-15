package cn.shmedo.monitor.monibotbaseapi.model.param.projectconfig;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroup;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ProjectGroupType;
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
public class SetProjectConfigParam implements IConfigParam, IConfigGroup, ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "项目ID不能为空")
    @Min(value = 1, message = "项目ID不能小于1")
    private Integer projectID;
    @NotEmpty(message = "要配置的key不能为空")
    private String key;
    @NotEmpty(message = "要配置的group不能为空")
    private String group;
    @NotEmpty(message = "要配置的value不能为空")
    private String value;
    @NotNull(message = "暂时只允许配置分组的额外配置,因此monitorGroupID不能为null")
    private Integer monitorGroupID;
    @JsonIgnore
    private TbProjectInfo tbProjectInfo;
    @JsonIgnore
    private TbMonitorGroup tbMonitorGroup;
    @JsonIgnore
    private ProjectGroupType projectGroupType;

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
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
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "要配置的分组不存在");
            }
            tbMonitorGroup = tbMonitorGroups.get(0);
            switch (projectGroupType) {
                case MONITOR_GROUP -> key = key + "::" + monitorGroupID;
                default -> log.info("未设置对应的分级枚举，将不会拼接key");
            }
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
