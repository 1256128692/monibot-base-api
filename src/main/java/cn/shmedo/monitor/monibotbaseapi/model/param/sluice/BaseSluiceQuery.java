package cn.shmedo.monitor.monibotbaseapi.model.param.sluice;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ProjectType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.sluice.ControlType;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Chengfs on 2023/11/21
 */
@Data
public class BaseSluiceQuery implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotNull
    @Positive
    private Integer companyID;

    private ControlType controlType;

    private String keyword;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime begin;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime end;

    @NotNull
    @Range(min = 1, max = 100)
    private Integer pageSize;

    @NotNull
    @Positive
    private Integer currentPage;

    @JsonIgnore
    private Collection<Integer> projectList;

    @Override
    public ResultWrapper<?> validate() {
        //有权限 且类型为水闸的项目
        Optional.ofNullable(PermissionUtil.getHavePermissionProjectList(companyID)).filter(e -> !e.isEmpty())
                .ifPresent(e -> {
                    TbProjectInfoMapper mapper = ContextHolder.getBean(TbProjectInfoMapper.class);
                    this.projectList = mapper.selectList(Wrappers.<TbProjectInfo>lambdaQuery().in(TbProjectInfo::getID, e)
                            .eq(TbProjectInfo::getProjectType, ProjectType.SLUICE.getCode())
                            .select(TbProjectInfo::getID)).stream().map(TbProjectInfo::getID).collect(Collectors.toSet());
                });
        return null;
    }

    @Override
    public List<Resource> parameter() {
        return this.projectList.stream().map(e -> new Resource(e.toString(), ResourceType.BASE_PROJECT)).toList();
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }
}