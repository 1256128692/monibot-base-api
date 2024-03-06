package cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckTaskPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPoint;
import cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir.CheckTaskStatus;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import reactor.util.function.Tuple3;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Chengfs on 2024/3/6
 */
@Data
public class AbsDeleteCheckPoint implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotEmpty
    private Set<Integer> idList;

    @JsonIgnore
    private List<TbCheckPoint> original;

    @JsonIgnore
    private List<Tuple3<Integer, Integer, Integer>> pointStatus;

    @Override
    public ResultWrapper<?> validate() {
        this.idList = Optional.ofNullable(idList).orElse(Set.of()).stream()
                .filter(e -> e != null && e > 0).collect(Collectors.toSet());
        Assert.isFalse(idList.isEmpty(), () -> new InvalidParameterException("巡检点必须有效且不能为空"));

        this.original = SpringUtil.getBean(TbCheckPointMapper.class).selectList(Wrappers.<TbCheckPoint>lambdaQuery()
                .in(TbCheckPoint::getID, idList).select(TbCheckPoint::getProjectID));
        Optional.of(original).filter(r -> r.size() == idList.size())
                .orElseThrow(() -> new IllegalArgumentException("巡检点必须有效且不能为空"));

        return null;
    }

    public boolean validatePointStatus() {
        TbCheckTaskPointMapper taskPointMapper = SpringUtil.getBean(TbCheckTaskPointMapper.class);
        this.pointStatus = taskPointMapper.listPointStatus(idList);
        // 巡检点有正在执行的巡检任务，则不允许删除
        for (Tuple3<Integer, Integer, Integer> item : pointStatus) {
            if (CheckTaskStatus.PROCESSING.getCode().equals(item.getT3())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Resource> parameter() {
        return this.original.stream()
                .map(e -> new Resource(e.getProjectID().toString(), ResourceType.BASE_PROJECT)).toList();
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }
}