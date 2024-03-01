package cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint;

import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckPointGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPointGroup;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Optional;

/**
 * @author Chengfs on 2024/2/28
 */
@Data
public class AddCheckPointGroupRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    @Positive
    private Integer companyID;

    @NotNull
    @Positive
    private Integer serviceID;

    @NotBlank
    @Size(max = 10)
    private String name;

    private String exValue;

    @Override
    public ResultWrapper<?> validate() {
        TbCheckPointGroupMapper mapper = SpringUtil.getBean(TbCheckPointGroupMapper.class);
        Optional.of(mapper.exists(Wrappers.<TbCheckPointGroup>lambdaQuery()
                        .eq(TbCheckPointGroup::getCompanyID, companyID)
                        .eq(TbCheckPointGroup::getServiceID, serviceID)
                        .eq(TbCheckPointGroup::getName, name)))
                .filter(r -> !r)
                .orElseThrow(() -> new IllegalArgumentException("巡检组名称已存在"));
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public String toString() {
        return "AddCheckPointGroupRequest{" +
                "companyID=" + companyID +
                ", serviceID=" + serviceID +
                ", name='" + name + '\'' +
                ", exValue='" + exValue + '\'' +
                '}';
    }

    public TbCheckPointGroup toEntity() {
        TbCheckPointGroup entity = new TbCheckPointGroup();
        CurrentSubject subject = CurrentSubjectHolder.getCurrentSubject();
        entity.setCompanyID(companyID);
        entity.setServiceID(serviceID);
        entity.setName(name);
        entity.setExValue(exValue);
        entity.setCreateUserID(subject.getSubjectID());
        entity.setUpdateUserID(subject.getSubjectID());
        return entity;
    }
}