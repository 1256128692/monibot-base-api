package cn.shmedo.monitor.monibotbaseapi.model.param.dashboard;

import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectServiceRelationMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectServiceRelation;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Chengfs on 2024/1/30
 */
@Data
public class QueryReservoirWarnStatsByProjectParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotNull
    @Positive
    private Integer companyID;

    @Positive
    @NotNull
    private Integer platform;

    @JsonIgnore
    private Collection<Integer> projects;

    @Override
    public ResultWrapper<?> validate() {
        TbProjectServiceRelationMapper relationMapper = SpringUtil.getBean(TbProjectServiceRelationMapper.class);
        Set<Integer> platformProjectIds = relationMapper.selectList(Wrappers.<TbProjectServiceRelation>lambdaQuery()
                        .eq(TbProjectServiceRelation::getServiceID, platform))
                .stream().map(TbProjectServiceRelation::getProjectID).collect(Collectors.toSet());
        this.projects = PermissionUtil.getHavePermissionProjectList(companyID, platformProjectIds);
        return null;
    }

    @Override
    public List<Resource> parameter() {
        return projects.stream().map(e -> new Resource(e.toString(), ResourceType.BASE_PROJECT)).toList();
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }
}