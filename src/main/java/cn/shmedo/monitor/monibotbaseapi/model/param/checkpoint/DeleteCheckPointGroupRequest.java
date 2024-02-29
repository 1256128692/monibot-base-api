package cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint;

import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckPointGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPointGroup;
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
public class DeleteCheckPointGroupRequest implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotEmpty
    private Set<@NotNull @Positive Integer> idList;

    @JsonIgnore
    private List<TbCheckPointGroup> groupList;

    @Override
    public ResultWrapper<?> validate() {
        TbCheckPointGroupMapper mapper = SpringUtil.getBean(TbCheckPointGroupMapper.class);
        this.groupList = mapper.selectBatchIds(idList);

        Optional.of(groupList).filter(r -> r.size() == idList.size())
                .orElseThrow(() -> new IllegalArgumentException("存在巡检组不存在"));

        return null;
    }

    @Override
    public List<Resource> parameter() {
        return groupList.stream().map(e -> new Resource(e.getProjectID().toString(), ResourceType.BASE_PROJECT)).toList();
    }

    @Override
    public String toString() {
        return "DeleteCheckPointGroupRequest{" +
                "idList=" + idList +
                '}';
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }
}