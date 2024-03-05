package cn.shmedo.monitor.monibotbaseapi.model.param.checktask;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckTaskMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckTask;
import cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir.CheckTaskStatus;
import com.alibaba.nacos.shaded.org.checkerframework.checker.index.qual.Positive;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Chengfs on 2024/3/1
 */
@Data
public class DeleteCheckTaskRequest implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotEmpty
    private Set<@Positive Integer> idList;

    @JsonIgnore
    private  List<TbCheckTask> origins;

    @Override
    public ResultWrapper<?> validate() {
        this.idList = Optional.ofNullable(idList).orElse(Set.of()).stream()
                .filter(e -> e != null && e > 0).collect(Collectors.toSet());
        Assert.isFalse(idList.isEmpty(), () -> new InvalidParameterException("巡检任务必须有效且不能为空"));


        TbCheckTaskMapper mapper = SpringUtil.getBean(TbCheckTaskMapper.class);
        this.origins = mapper.selectList(Wrappers.<TbCheckTask>lambdaQuery()
                .in(TbCheckTask::getID, idList)
                .select(TbCheckTask::getID, TbCheckTask::getProjectID, TbCheckTask::getStatus));

        Assert.isTrue(origins.size() == idList.size(), () -> new InvalidParameterException("巡检任务必须有效且不能为空"));
        origins.forEach(task -> Assert.isTrue(task.getStatus() == CheckTaskStatus.UN_START,
                () -> new InvalidParameterException("只能删除未开始的巡检任务")));
        return null;
    }

    @Override
    public List<Resource> parameter() {
        return origins.stream().map(e -> new Resource(e.getProjectID().toString(), ResourceType.BASE_PROJECT)).toList();
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }
}