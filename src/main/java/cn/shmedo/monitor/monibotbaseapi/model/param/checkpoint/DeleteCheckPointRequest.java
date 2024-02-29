package cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint;

import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckTaskPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPoint;
import cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir.CheckTaskStatus;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Chengfs on 2024/2/28
 */
@Data
public class DeleteCheckPointRequest implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotEmpty
    private Set<@NotNull @Positive Integer> idList;

    @JsonIgnore
    private List<TbCheckPoint> original;

    @Override
    public ResultWrapper<?> validate() {
        TbCheckPointMapper mapper = SpringUtil.getBean(TbCheckPointMapper.class);
        this.original = mapper.selectList(Wrappers.<TbCheckPoint>lambdaQuery()
                .in(TbCheckPoint::getID, idList)
                .select(TbCheckPoint::getProjectID));

        Optional.of(original).filter(r -> r.size() == idList.size())
                .orElseThrow(() -> new IllegalArgumentException("包含不存在的巡检点"));

        TbCheckTaskPointMapper taskPointMapper = SpringUtil.getBean(TbCheckTaskPointMapper.class);
        if (taskPointMapper.existStatusByPointIds(idList, CheckTaskStatus.PROCESSING)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "所选的巡检点有正在执行的巡检任务，无法删除，请确认无正在执行的巡检任务再做删除。");
        }

        return null;
    }

    @Override
    public List<Resource> parameter() {
        return this.original.stream().map(e -> new Resource(e.getProjectID().toString(), ResourceType.BASE_PROJECT)).toList();
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }

    @Override
    public String toString() {
        return "DeleteCheckPointRequest{" +
                "idList=" + idList +
                '}';
    }
}