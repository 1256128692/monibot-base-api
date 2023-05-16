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
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectConfig;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ProjectGroupType;
import cn.shmedo.monitor.monibotbaseapi.util.projectConfig.ProjectConfigKeyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
    /**
     * @see #getSubSum()
     */
    @JsonIgnore
    private Map<Integer, TbMonitorGroup> tbMonitorGroupMap;
    /**
     * @see #getSubSum()
     */
    @JsonIgnore
    private Map<Integer, TbMonitorPoint> tbMonitorPointMap;

    @SuppressWarnings("ResultOfMethodCallIgnored")
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
                .peek(u -> u.setProjectID(projectID)).collect(Collectors.groupingBy(ProjectGroupType::getProjectGroupType));
        projectGroupTypeListMap.entrySet().stream().peek(u -> {
            switch (u.getKey()) {
                // 需要在这里、SetProjectConfigParam和IConfigID进行处理
                case MONITOR_GROUP -> {
                    TbMonitorGroupMapper groupMapper = ContextHolder.getBean(TbMonitorGroupMapper.class);
                    tbMonitorGroupMap = groupMapper.selectList(new LambdaQueryWrapper<TbMonitorGroup>()
                            .in(TbMonitorGroup::getID, u.getValue().stream().map(SetProjectConfigParam::getMonitorGroupID)
                                    .toList())).stream().collect(Collectors.toMap(TbMonitorGroup::getID, w -> w));
                    u.getValue().stream().peek(w -> ProjectConfigKeyUtils.setKey(w, w.getMonitorGroupID())).toList();
                }
                case MONITOR_POINT -> {
                    TbMonitorPointMapper pointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
                    tbMonitorPointMap = pointMapper.selectList(new LambdaQueryWrapper<TbMonitorPoint>()
                            .in(TbMonitorPoint::getID, u.getValue().stream().map(SetProjectConfigParam::getMonitorPointID)
                                    .toList())).stream().collect(Collectors.toMap(TbMonitorPoint::getID, w -> w));
                    u.getValue().stream().peek(w -> ProjectConfigKeyUtils.setKey(w, w.getMonitorPointID())).toList();
                }
                default ->
                        throw new IllegalArgumentException("switch in {@code BatchSetProjectConfigParam} lack enums,@see ProjectGroupType");
            }
        }).toList();
        // 同一个项目同一级对同一个被配置对象仅能配置一次
        // e.g.
        // 表定义(group,key,value) -> dataList中数据项(monitorGroup,stConfig::123,value),
        // {@code dataList}里只能出现一次(monitorGroup,stConfig::123,value),所有的value变更都要在这个value里
        if (dataList.size() != getSubSum()) {
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

    /**
     * 批量配置时，可能会有多级同时配置，所以校验时需要校验被配置对象size(即所有存有被配置对象map的keySet.size()之和)等于dataList.size()
     */
    private Integer getSubSum() {
        return (Objects.isNull(tbMonitorGroupMap) ? 0 : tbMonitorGroupMap.keySet().size())
                + (Objects.isNull(tbMonitorPointMap) ? 0 : tbMonitorPointMap.keySet().size());
    }

    public static BatchSetProjectConfigParam singleToBatch(SetProjectConfigParam param) {
        BatchSetProjectConfigParam res = new BatchSetProjectConfigParam();
        res.setProjectID(param.getProjectID());
        res.setDataList(List.of(param));
        Optional.ofNullable(param.getTbMonitorGroup()).map(u -> Map.of(u.getID(), u)).ifPresent(res::setTbMonitorGroupMap);
        Optional.ofNullable(param.getTbMonitorPoint()).map(u -> Map.of(u.getID(), u)).ifPresent(res::setTbMonitorPointMap);
        return res;
    }

    public List<TbProjectConfig> build() {
        return dataList.stream().map(u -> {
            TbProjectConfig config = new TbProjectConfig();
            BeanUtils.copyProperties(u, config);
            config.setID(u.getConfigID());
            return config;
        }).toList();
    }
}
