package cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint;

import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
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
public class DeleteCheckPointGroupRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotEmpty
    private Set<@NotNull @Positive Integer> idList;

    @JsonIgnore
    private Integer companyID;

    @Override
    public ResultWrapper<?> validate() {
        TbCheckPointGroupMapper mapper = SpringUtil.getBean(TbCheckPointGroupMapper.class);
        List<TbCheckPointGroup> groupList = mapper.selectBatchIds(idList);

        Optional.of(groupList).filter(r -> r.size() == idList.size())
                .orElseThrow(() -> new IllegalArgumentException("存在巡检组不存在"));

        List<Integer> list = groupList.stream().map(TbCheckPointGroup::getCompanyID).distinct().toList();
        Optional.of(list)
                .filter(r -> r.size() == 1)
                .orElseThrow(() -> new IllegalArgumentException("存在巡检组不属于同一公司"));

        this.companyID = list.get(0);
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public String toString() {
        return "DeleteCheckPointGroupRequest{" +
                "idList=" + idList +
                '}';
    }
}