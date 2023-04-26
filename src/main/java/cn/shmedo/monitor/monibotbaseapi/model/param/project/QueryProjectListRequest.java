package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PlatformType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryProjectListRequest implements ParameterValidator, ResourcePermissionProvider<Resource> {

    @Range(min = 1, max = 100, message = "分页大小必须在1-100之间")
    @NotNull(message = "pageSize不能为空")
    private Integer pageSize;

    @Range(min = 1, message = "当前页码必须大于0")
    @NotNull(message = "currentPage不能为空")
    private Integer currentPage;

    private String projectName;

    private String directManageUnit;

    private String location;

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    private Byte projectType;

    private Boolean enable;

    private List<Byte> platformTypeList;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date expiryDate;

    //@Past(message = "开始时间不能大于当前时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginCreateTime;

    //@Past(message = "结束时间不能大于当前时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endCreateTime;

    @JsonIgnore
    private List<Integer> projectIDList;

    private List<Property> propertyEntity;

    @Data
    public static class Property {

        @NotBlank(message = "属性名称不能为空")
        private String name;

        private String value;

        @JsonIgnore
        private List<String> values;

        public Property(String s, String v) {
            this.name = s;
            this.setValue(v);
        }

        public void setValue(String value) {
            this.value = value;
            if (JSONUtil.isTypeJSONArray(value)) {
                this.values = JSONUtil.toList(JSONUtil.parseArray(value), String.class);
            } else {
                this.values = List.of(value);
            }
        }

        public Property() {
            this.values = new ArrayList<>();
        }
    }

    @Override
    public ResultWrapper<?> validate() {
        if (projectType != null) {
            if (!ProjectTypeCache.projectTypeMap.containsKey(projectType)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目类型不存在");
            }
        }
        if (beginCreateTime != null && endCreateTime != null) {
            if (beginCreateTime.getTime() > endCreateTime.getTime()) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "开始时间不能大于结束时间");
            }
        }

        if (CollUtil.isNotEmpty(platformTypeList)) {
            if (platformTypeList.stream().anyMatch(platformType -> !PlatformType.validate(platformType))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "平台类型不存在");
            }
        }

        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(this.companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionProvider.super.resourcePermissionType();
    }
}
