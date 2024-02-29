package cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint;

import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckPointGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPointGroup;
import com.alibaba.nacos.shaded.org.checkerframework.checker.index.qual.Positive;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Chengfs on 2024/2/28
 */
@Data
public class BatchUpdateCheckPointRequest implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotEmpty
    private Set<@NonNull @Positive Integer> idList;

    private Boolean enable;

    @Positive
    private Integer groupID;

    @JsonIgnore
    protected List<TbCheckPoint> original;

    @Override
    public ResultWrapper<?> validate() {
        if (enable != null || groupID != null) {

            Optional.ofNullable(groupID).ifPresent(e -> {
                TbCheckPointGroupMapper mapper = SpringUtil.getBean(TbCheckPointGroupMapper.class);
                Optional.of(mapper.exists(Wrappers.<TbCheckPointGroup>lambdaQuery()
                                .eq(TbCheckPointGroup::getID, e))).filter(r -> r)
                        .orElseThrow(() -> new InvalidParameterException("巡检组不存在"));
            });

            TbCheckPointMapper mapper = SpringUtil.getBean(TbCheckPointMapper.class);
            this.original = mapper.selectList(Wrappers.<TbCheckPoint>lambdaQuery()
                    .in(TbCheckPoint::getID, idList)
                    .eq(TbCheckPoint::getEnable, true)
                    .select(TbCheckPoint::getID, TbCheckPoint::getGroupID, TbCheckPoint::getProjectID));


            Optional.of(original)
                    .filter(e -> e.size() == idList.size())
                    .filter(e -> {
                        if (groupID != null) {
                            Optional.of(e).filter(list -> list.stream().allMatch(e1 -> e1.getGroupID() == null ||
                                            e1.getGroupID().equals(groupID)))
                                    .orElseThrow(() -> new InvalidParameterException("存在巡检点已绑定其他巡检组"));
                        }
                        return true;
                    })
                    .orElseThrow(() -> new InvalidParameterException("存在巡检点不存在或未启用"));
            return null;
        }
        return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "缺少有效参数");
    }

    @Override
    public List<Resource> parameter() {
        return original.stream().map(TbCheckPoint::getProjectID).distinct()
                .map(e -> new Resource(e.toString(), ResourceType.BASE_PROJECT)).toList();
    }

    @Override
    public String toString() {
        return "BatchUpdateCheckPointRequest{" +
                "idList=" + idList +
                ", enable=" + enable +
                ", groupID=" + groupID +
                '}';
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }

    public List<TbCheckPoint> toEntitys() {
        CurrentSubject subject = CurrentSubjectHolder.getCurrentSubject();
        return idList.stream().map(e -> {
            TbCheckPoint entity = new TbCheckPoint();
            entity.setID(e);
            entity.setEnable(enable);
            entity.setGroupID(groupID);
            entity.setUpdateUserID(subject.getSubjectID());
            return entity;
        }).toList();
    }
}