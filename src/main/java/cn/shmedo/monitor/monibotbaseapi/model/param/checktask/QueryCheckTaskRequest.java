package cn.shmedo.monitor.monibotbaseapi.model.param.checktask;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckTaskMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckTask;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chengfs on 2024/3/1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryCheckTaskRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    @Positive
    private Integer id;

    @JsonIgnore
    private TbCheckTask entity;

    @Override
    public ResultWrapper<?> validate() {

        TbCheckTaskMapper mapper = SpringUtil.getBean(TbCheckTaskMapper.class);
        this.entity = mapper.selectById(id);
        Assert.isTrue(this.entity != null, () -> new InvalidParameterException("巡检任务不存在"));

        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.entity.getProjectID().toString(), ResourceType.BASE_PROJECT);
    }
}