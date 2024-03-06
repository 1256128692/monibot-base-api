package cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckPointGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckTaskPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPointGroup;
import cn.shmedo.monitor.monibotbaseapi.model.enums.reservoir.CheckTaskStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Chengfs on 2024/2/28
 */
@Data
public class AbsDeleteCheckPointGroup implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotEmpty
    private Set<Integer> idList;

    @JsonIgnore
    private Integer companyID;

    private List<Tuple2<Integer, Integer>> groupStatus;

    @Override
    public ResultWrapper<?> validate() {
        this.idList = Optional.ofNullable(idList).orElse(Set.of()).stream()
                .filter(e -> e != null && e > 0).collect(Collectors.toSet());
        Assert.isFalse(idList.isEmpty(), () -> new InvalidParameterException("巡检组必须有效且不能为空"));

        List<TbCheckPointGroup> groupList = SpringUtil.getBean(TbCheckPointGroupMapper.class).selectBatchIds(idList);

        Optional.of(groupList).filter(r -> r.size() == idList.size())
                .orElseThrow(() -> new IllegalArgumentException("巡检组必须有效且不能为空"));

        List<Integer> list = groupList.stream().map(TbCheckPointGroup::getCompanyID).distinct().toList();
        Optional.of(list)
                .filter(r -> r.size() == 1)
                .orElseThrow(() -> new IllegalArgumentException("巡检组必须属于同一公司"));

        this.companyID = list.get(0);
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.companyID.toString(), ResourceType.COMPANY);
    }

    public boolean validateGroupStatus() {
        TbCheckTaskPointMapper taskPointMapper = SpringUtil.getBean(TbCheckTaskPointMapper.class);
        this.groupStatus = taskPointMapper.listPointGroupStatus(idList);
        // 巡检组有正在执行的巡检任务，则不允许删除
        for (Tuple2<Integer, Integer> item : groupStatus) {
            if (CheckTaskStatus.PROCESSING.getCode().equals(item.getT2())) {
                return false;
            }
        }
        return true;
    }
}