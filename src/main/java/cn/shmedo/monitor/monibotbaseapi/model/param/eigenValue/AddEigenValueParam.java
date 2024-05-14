package cn.shmedo.monitor.monibotbaseapi.model.param.eigenValue;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbEigenValue;
import cn.shmedo.monitor.monibotbaseapi.model.enums.ScopeType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
public class AddEigenValueParam extends TbEigenValue implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @JsonIgnore
    private Integer id;

    @Positive
    private Integer projectID;

    @NotNull(message = "作用域scope不能为空")
    private ScopeType scope;

    @Positive
    @NotNull(message = "监测项目ID不能为空")
    private Integer monitorItemID;

    @Positive
    @NotNull(message = "监测类型子类型monitorTypeFieldID不能为空")
    private Integer monitorTypeFieldID;

    @NotBlank(message = "名称不能为空")
    private String name;

    @NotNull(message = "值不能为空")
    private Double value;

    @Positive
    @NotNull(message = "单位ID不能为空")
    private Integer unitID;

    private String exValue;

    @Valid
    private List<@Positive @NotNull Integer> monitorPointIDList;

    @JsonIgnore
    private Boolean allMonitorPoint;

    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private Integer createUserID;
    @JsonIgnore
    private Integer updateUserID;


    @Override
    public ResultWrapper<?> validate() {

        Assert.notNull(projectID, "工程ID不能为空");
        AddEigenValueListParam.validate(this.projectID, List.of(this));


        this.allMonitorPoint = CollectionUtils.isEmpty(monitorPointIDList);
        Optional.ofNullable(CurrentSubjectHolder.getCurrentSubject()).ifPresent(subject -> {
                    this.updateUserID = subject.getSubjectID();
                    this.createUserID = subject.getSubjectID();
                });
        Date now = DateUtil.date();
        this.updateTime = now;
        this.createTime = now;

        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }
}
