package cn.shmedo.monitor.monibotbaseapi.model.param.checkpoint;

import cn.hutool.extra.spring.SpringUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.exception.InvalidParameterException;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbCheckPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbCheckPoint;
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

    @NotBlank
    @Size(max = 10)
    private String name;

    @NotBlank
    @Size(max = 255)
    private String address;

    @NotBlank
    @Size(max = 255)
    private String location;

    @Override
    public ResultWrapper<?> validate() {
        this.name = name.trim();
        this.address = address.trim();
        this.location = location.trim();

        TbCheckPointMapper mapper = SpringUtil.getBean(TbCheckPointMapper.class);
        boolean exists = mapper.exists(Wrappers.<TbCheckPoint>lambdaQuery()
                .eq(TbCheckPoint::getProjectID, projectID)
                .eq(TbCheckPoint::getName, name));
        Optional.of(exists).filter(r -> !r).orElseThrow(() -> new InvalidParameterException("名称: " + name + " 已存在"));
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
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public TbCheckPoint toTbCheckPoint() {
        CurrentSubject subject = CurrentSubjectHolder.getCurrentSubject();
        TbCheckPoint entity = new TbCheckPoint();
        entity.setProjectID(projectID);
        entity.setName(name);
        entity.setAddress(address);
        entity.setLocation(location);
        entity.setCreateUserID(subject.getSubjectID());
        entity.setUpdateUserID(subject.getSubjectID());
        return entity;
    }
}