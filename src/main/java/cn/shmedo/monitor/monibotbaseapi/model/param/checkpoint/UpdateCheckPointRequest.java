package cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckPointGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPointGroup;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Optional;

/**
 * @author Chengfs on 2024/2/28
 */
@Data
public class UpdateCheckPointRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    @Positive
    private Integer id;

    @Size(max = 10)
    private String name;

    @Size(max = 255)
    private String address;

    @Size(max = 255)
    private String location;

    private Boolean enable;

    @Positive
    private Integer groupID;

    @JsonIgnore
    private TbCheckPoint original;

    @Override
    public ResultWrapper<?> validate() {

        boolean isUpdate = name != null || address != null || location != null || enable != null || groupID != null;
        if (isUpdate) {
            TbCheckPointMapper mapper = SpringUtil.getBean(TbCheckPointMapper.class);
            this.original = mapper.selectOne(Wrappers.<TbCheckPoint>lambdaQuery()
                    .eq(TbCheckPoint::getID, id)
                    .select(TbCheckPoint::getProjectID, TbCheckPoint::getName));
            Optional.ofNullable(original).orElseThrow(() -> new IllegalArgumentException("巡检点不存在"));

            //校验名称
            this.name = Optional.ofNullable(name).map(String::trim).orElse(StrUtil.EMPTY);
            this.address = Optional.ofNullable(address).map(String::trim).orElse(StrUtil.EMPTY);
            this.location = Optional.ofNullable(location).map(String::trim).orElse(StrUtil.EMPTY);
            if (!name.isBlank() && !original.getName().equals(name)) {
                Optional.of(mapper.exists(Wrappers.<TbCheckPoint>lambdaQuery()
                                .eq(TbCheckPoint::getProjectID, original.getProjectID())
                                .eq(TbCheckPoint::getServiceID, original.getServiceID())
                                .eq(TbCheckPoint::getName, name)))
                        .filter(r -> !r)
                        .orElseThrow(() -> new InvalidParameterException("名称:" + name + "已存在"));
            }

            //
            Optional.ofNullable(groupID).ifPresent(id -> {
                TbCheckPointGroupMapper groupMapper = SpringUtil.getBean(TbCheckPointGroupMapper.class);
                TbCheckPointGroup group = groupMapper.selectById(id);
                Assert.isTrue(group != null, () -> new InvalidParameterException("分组不存在"));
                Assert.isTrue(group.getServiceID().equals(original.getServiceID()), () -> new InvalidParameterException("分组不属于该平台"));
            });

            return null;
        }
        return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER);
    }

    @Override
    public Resource parameter() {
        return new Resource(original.getProjectID().toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public String toString() {
        return "UpdateCheckPointRequest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", location='" + location + '\'' +
                ", enable=" + enable +
                ", groupID=" + groupID +
                '}';
    }

    public TbCheckPoint toEntity() {
        TbCheckPoint entity = new TbCheckPoint();
        entity.setID(id);
        entity.setUpdateUserID(CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        Optional.ofNullable(this.name).filter(e -> !e.isBlank() && !e.equals(original.getName())).ifPresent(entity::setName);
        Optional.ofNullable(this.address).filter(e -> !e.isBlank()).ifPresent(entity::setAddress);
        Optional.ofNullable(this.location).filter(e -> !e.isBlank()).ifPresent(entity::setLocation);
        Optional.ofNullable(this.enable).ifPresent(entity::setEnable);
        Optional.ofNullable(this.groupID).ifPresent(entity::setGroupID);
        return entity;
    }
}