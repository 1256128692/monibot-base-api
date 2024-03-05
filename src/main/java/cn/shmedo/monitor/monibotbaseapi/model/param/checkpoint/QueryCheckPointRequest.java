package cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint;

import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPoint;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Optional;

/**
 * @author Chengfs on 2024/2/28
 */
@Data
public class QueryCheckPointRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    @Positive
    private Integer id;

    @JsonIgnore
    private TbCheckPoint entity;

    @Override
    public ResultWrapper<?> validate() {
        this.entity = SpringUtil.getBean(TbCheckPointMapper.class)
                .selectById(id);
        Optional.ofNullable(entity).orElseThrow(() -> new InvalidParameterException("巡检点不存在"));
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.entity.getProjectID().toString(), ResourceType.BASE_PROJECT);
    }
}