package cn.shmedo.monitor.monibotbaseapi.model.param.sluice;

import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * @author Chengfs on 2023/11/21
 */
@Data
public class SingleSluiceRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    @Positive
    private Integer projectID;

    @JsonIgnore
    private TbProjectInfo project;

    @Override
    public ResultWrapper<?> validate() {
        TbProjectInfoMapper projectMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        this.project = projectMapper.selectById(projectID);

        Assert.notNull(project, "项目不存在");
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }
}