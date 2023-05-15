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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-12 18:29
 */
@Data
public class BatchSetProjectConfigParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "项目ID不能为空")
    @Min(value = 1, message = "项目ID不能小于1")
    private Integer projectID;
    @NotEmpty(message = "要配置的参数不能为空")
    private List<SetProjectConfigParam> dataList;
    @JsonIgnore
    private TbProjectInfo tbProjectInfo;
    @JsonIgnore
    private Map<Integer, TbMonitorGroup> tbMonitorGroupMap;

    @SuppressWarnings({"SwitchStatementWithTooFewBranches", "ResultOfMethodCallIgnored"})
    @Override
    public ResultWrapper validate() {
        //如果只有一个，按单个校验处理
        if (dataList.size() == 1) {
            SetProjectConfigParam param = dataList.get(0);
            ResultWrapper validate = param.validate();
            this.tbProjectInfo = param.getTbProjectInfo();
            return validate;
        }
        TbProjectInfoMapper mapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        List<TbProjectInfo> infos = mapper.selectList(new LambdaQueryWrapper<TbProjectInfo>()
                .eq(TbProjectInfo::getID, projectID));
        if (CollectionUtil.isEmpty(infos)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有项目不存在");
        }
        tbProjectInfo = infos.get(0);
        Map<ProjectGroupType, List<SetProjectConfigParam>> projectGroupTypeListMap = dataList.stream()
                .collect(Collectors.groupingBy(ProjectGroupType::getProjectGroupType));
        projectGroupTypeListMap.entrySet().stream().peek(u -> {
            switch (u.getKey()) {
                // 目前只有一个monitorGroup可进行配置，如果有其他，需要在这里、SetProjectConfigParam和IConfigGroup进行处理
                case MONITOR_GROUP -> {
                    TbMonitorGroupMapper groupMapper = ContextHolder.getBean(TbMonitorGroupMapper.class);
                    tbMonitorGroupMap = groupMapper.selectList(new LambdaQueryWrapper<TbMonitorGroup>()
                            .in(TbMonitorGroup::getID, u.getValue().stream().map(SetProjectConfigParam::getMonitorGroupID)
                                    .toList())).stream().collect(Collectors.toMap(TbMonitorGroup::getID, w -> w));
                }
                default ->
                        throw new RuntimeException("switch in {@code BatchSetProjectConfigParam} lack enums,@see ProjectGroupType");
            }
        }).toList();
        // 同一个项目每一级仅能对同一个项目配置一次
        // 被配置对象的size之和(目前这里只有一个tbMonitorGroupMap)等于setProjectConfigParamList.size()
        if (dataList.size() != tbMonitorGroupMap.keySet().size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "被配置对象不存在或重复");
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
