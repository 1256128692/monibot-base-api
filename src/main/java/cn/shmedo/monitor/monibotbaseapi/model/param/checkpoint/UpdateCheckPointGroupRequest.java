package cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckPointGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPointGroup;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Optional;

/**
 * @author Chengfs on 2024/2/28
 */
@Data
public class UpdateCheckPointGroupRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    @Positive
    private Integer id;

    private String name;

    private String exValue;

    @JsonIgnore
    protected TbCheckPointGroup original;

    @Override
    public ResultWrapper<?> validate() {
        if (StrUtil.isNotBlank(name) || StrUtil.isNotBlank(exValue)) {
            TbCheckPointGroupMapper mapper = SpringUtil.getBean(TbCheckPointGroupMapper.class);
            this.original = mapper.selectById(id);
            Optional.ofNullable(original).orElseThrow(() -> new IllegalArgumentException("巡检组必须有效且不能为空"));

            this.name = Optional.ofNullable(name).map(String::trim).orElse(StrUtil.EMPTY);
            if (!name.isBlank() && !name.equals(original.getName())) {
                Optional.of(mapper.exists(Wrappers.<TbCheckPointGroup>lambdaQuery()
                                .eq(TbCheckPointGroup::getName, name)
                                .eq(TbCheckPointGroup::getCompanyID, original.getCompanyID())
                                .eq(TbCheckPointGroup::getServiceID, original.getServiceID())))
                        .filter(r -> !r)
                        .orElseThrow(() -> new IllegalArgumentException("巡检组名称不能重复"));
            }
            return null;
        }
        return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "缺少必要参数");
    }

    @Override
    public Resource parameter() {
        return new Resource(original.getCompanyID().toString(), ResourceType.COMPANY);
    }

    @Override
    public String toString() {
        return "UpdateCheckPointGroupRequest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", exValue='" + exValue + '\'' +
                '}';
    }

    public TbCheckPointGroup toEntity() {
        TbCheckPointGroup entity = new TbCheckPointGroup();
        entity.setID(id);
        entity.setUpdateUserID(CurrentSubjectHolder.getCurrentSubject().getSubjectID());
        Optional.ofNullable(this.name).filter(e -> !e.isBlank() && !e.equals(original.getName()))
                .ifPresent(entity::setName);
        Optional.ofNullable(this.exValue).ifPresent(entity::setExValue);
        return entity;
    }
}