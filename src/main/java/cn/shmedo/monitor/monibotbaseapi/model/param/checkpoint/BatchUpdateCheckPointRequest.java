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
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Chengfs on 2024/2/28
 */
@Data
public class BatchUpdateCheckPointRequest implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    private Set<Integer> idList;

    private Boolean enable;

    @Positive
    private Integer groupID;

    @JsonIgnore
    protected List<TbCheckPoint> original;

    @Override
    public ResultWrapper<?> validate() {
        if (enable != null || groupID != null) {

            this.idList = Optional.ofNullable(idList).orElse(Set.of()).stream()
                    .filter(e -> e != null && e > 0).collect(Collectors.toSet());

            //当 enable 不为null时，必须指定巡检点
            Assert.isTrue(enable == null || !idList.isEmpty(), () -> new InvalidParameterException("巡检点必须有效且不能为空"));
            this.original = idList.isEmpty() ? List.of() :
                    SpringUtil.getBean(TbCheckPointMapper.class).selectList(Wrappers.<TbCheckPoint>lambdaQuery()
                            .in(TbCheckPoint::getID, idList)
                            .select(TbCheckPoint::getID, TbCheckPoint::getGroupID,
                                    TbCheckPoint::getProjectID, TbCheckPoint::getServiceID));

            //检查选中的巡检点是否存在
            Assert.isTrue(original.size() == idList.size(), () -> new InvalidParameterException("巡检点必须有效且不能为空"));


            if (groupID != null) {
                TbCheckPointGroup group = SpringUtil.getBean(TbCheckPointGroupMapper.class).selectById(groupID);
                //校验巡检组是否存在、巡检组是否已绑定巡检组、巡检点和巡检组所属服务是否一致
                Assert.notNull(group, () -> new InvalidParameterException("巡检组不存在"));
                this.original.forEach(item -> {
                    Assert.isTrue(item.getGroupID() == null || item.getGroupID().equals(groupID),
                            () -> new InvalidParameterException("不能包含属于其他巡检组的点"));
                    Assert.isTrue(item.getServiceID().equals(group.getServiceID()),
                            () -> new InvalidParameterException("巡检点与巡检分组必须属于同一平台"));
                });
            }
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