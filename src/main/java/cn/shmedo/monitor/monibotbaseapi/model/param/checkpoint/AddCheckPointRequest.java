package cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint;

import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectServiceRelationMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectServiceRelation;
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
public class AddCheckPointRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @NotNull
    @Positive
    private Integer companyID;

    @NotNull
    @Positive
    private Integer projectID;

    @NotNull
    @Positive
    private Integer serviceID;

    @NotBlank
    @Size(max = 10)
    private String name;

    @NotBlank
    @Size(max = 255)
    private String address;

    @NotBlank
    @Size(max = 255)
    private String location;

    private Boolean enable;

    @Override
    public ResultWrapper<?> validate() {
        this.name = name.trim();
        this.address = address.trim();
        this.location = location.trim();
        this.enable = Optional.ofNullable(this.enable).orElse(Boolean.TRUE);

        TbCheckPointMapper mapper = SpringUtil.getBean(TbCheckPointMapper.class);
        boolean exists = mapper.exists(Wrappers.<TbCheckPoint>lambdaQuery()
                .eq(TbCheckPoint::getProjectID, projectID)
                .eq(TbCheckPoint::getServiceID, serviceID)
                .eq(TbCheckPoint::getName, name));
        Optional.of(exists).filter(r -> !r).orElseThrow(() -> new InvalidParameterException("巡检组名称已存在"));

        TbProjectServiceRelationMapper projectRelationMapper = SpringUtil.getBean(TbProjectServiceRelationMapper.class);
        Assert.isTrue(projectRelationMapper.exists(Wrappers.<TbProjectServiceRelation>lambdaQuery()
                        .eq(TbProjectServiceRelation::getProjectID, projectID)
                        .eq(TbProjectServiceRelation::getServiceID, serviceID)),
                () -> new InvalidParameterException("所选项目必须属于所选平台"));
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public String toString() {
        return "AddCheckPointRequest{" +
                "companyID=" + companyID +
                ", projectID=" + projectID +
                ", serviceID=" + serviceID +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public TbCheckPoint toTbCheckPoint() {
        CurrentSubject subject = CurrentSubjectHolder.getCurrentSubject();
        TbCheckPoint entity = new TbCheckPoint();
        entity.setProjectID(projectID);
        entity.setServiceID(serviceID);
        entity.setName(name);
        entity.setAddress(address);
        entity.setLocation(location);
        entity.setEnable(enable);
        entity.setCreateUserID(subject.getSubjectID());
        entity.setUpdateUserID(subject.getSubjectID());
        return entity;
    }
}