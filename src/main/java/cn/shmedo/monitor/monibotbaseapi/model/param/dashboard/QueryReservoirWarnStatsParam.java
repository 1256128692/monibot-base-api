package cn.shmedo.monitor.monibotbaseapi.model.param.dashboard;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectServiceRelationMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectServiceRelation;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Chengfs on 2024/1/25
 */
@Data
public class QueryReservoirWarnStatsParam implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotNull
    @Positive
    private Integer companyID;

    @Positive
    private Integer projectID;

    @Positive
    private Integer platform;

    @JsonIgnore
    private Collection<Integer> projects;

    @Override
    public ResultWrapper<?> validate() {
        if (platform != null)  {
            TbProjectServiceRelationMapper relationMapper = SpringUtil.getBean(TbProjectServiceRelationMapper.class);
            LambdaQueryWrapper<TbProjectServiceRelation> query = Wrappers.<TbProjectServiceRelation>lambdaQuery()
                    .eq(TbProjectServiceRelation::getServiceID, platform);
            Optional.ofNullable(projectID).ifPresent(pid -> query.eq(TbProjectServiceRelation::getProjectID, pid));

            this.projects = relationMapper.selectList(query)
                    .stream().map(TbProjectServiceRelation::getProjectID).collect(Collectors.toSet());

            Optional.ofNullable(projectID).ifPresent(pid -> Assert.isTrue(projects.contains(pid),
                    () -> new InvalidParameterException("项目 " + pid + " 不属于平台" + platform)));

            if (projects.isEmpty()) {
                return null;
            }
        } else if (projectID != null) {
            this.projects = List.of(projectID);
        }

        projects = PermissionUtil.getHavePermissionProjectList(companyID, projects);
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