package cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint;

import cn.hutool.core.lang.Assert;
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

            TbCheckPointMapper mapper = SpringUtil.getBean(TbCheckPointMapper.class);
            this.original = mapper.selectList(Wrappers.<TbCheckPoint>lambdaQuery()
                    .in(TbCheckPoint::getID, idList).select(TbCheckPoint::getID, TbCheckPoint::getGroupID,
                            TbCheckPoint::getProjectID, TbCheckPoint::getServiceID));
            Assert.isTrue(original.size() == idList.size(), () -> new InvalidParameterException("包含不存在的巡检点"));
            original.forEach(item -> Assert.isTrue(item.getGroupID() == null || item.getGroupID().equals(groupID),
                    () -> new InvalidParameterException("包含巡检点已绑定其他巡检组")));

            Optional.ofNullable(groupID).ifPresent(e -> {
                TbCheckPointGroupMapper groupMapper = SpringUtil.getBean(TbCheckPointGroupMapper.class);
                TbCheckPointGroup group = groupMapper.selectById(e);
                Assert.notNull(group, () -> new InvalidParameterException("巡检组不存在"));

                //校验巡检点所属服务 与 巡检组所属服务是否一致
                this.original.forEach(item -> Assert.isTrue(item.getServiceID().equals(group.getServiceID()),
                        () -> new InvalidParameterException("巡检点与巡检分组不属于同一平台")));
            });

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