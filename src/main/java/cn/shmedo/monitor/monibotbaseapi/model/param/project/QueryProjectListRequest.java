package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PlatformType;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.PageUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.*;

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


    private Boolean creatTimeAsc;

    private String queryCode;

    private String projectName;

    private String location;

    @NotNull(message = "公司ID不能为空")
    private Integer companyID;

    private Byte projectType;

    private Boolean enable;

    private Boolean isSonLevel;

    private List<Byte> platformTypeList;
    private List<String> platformTypeSet;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date expiryDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date expiryDateBegin;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date expiryDateEnd;

    //@Past(message = "开始时间不能大于当前时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date beginCreateTime;

    //@Past(message = "结束时间不能大于当前时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endCreateTime;

    @JsonIgnore
    private Collection<Integer> projectIDList;

    private List<Property> propertyEntity;

    @Override
    public ResultWrapper<?> validate() {
        Optional.ofNullable(projectType).ifPresent(type -> {
            Assert.isTrue(ProjectTypeCache.projectTypeMap.containsKey(type), "项目类型不存在");
        });

        if (beginCreateTime != null && endCreateTime != null) {
            Assert.isTrue(beginCreateTime.getTime() < endCreateTime.getTime(), "开始时间不能大于结束时间");
        }

        Optional.ofNullable(platformTypeList).filter(e -> !e.isEmpty()).ifPresent(e -> {
            Assert.isTrue(e.stream().allMatch(PlatformType::validate), "平台类型不存在");
        });

        // 获取有权限的项目列表
        this.projectIDList = PermissionUtil.getHavePermissionProjectList(companyID);
        if (CollUtil.isNotEmpty(projectIDList)) {
            //查询满足过滤条件且有权限的项目ID
            Optional.ofNullable(propertyEntity).filter(e -> !e.isEmpty())
                    .ifPresent(props -> {
                        TbProjectInfoMapper projectInfoMapper = SpringUtil.getBean(TbProjectInfoMapper.class);
                        this.projectIDList = projectInfoMapper.getProjectIDByProperty(props, projectIDList);
                    });
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

        public Property() {
            this.values = new ArrayList<>();
        }

        public void setValue(String value) {
            this.value = value;
            if (JSONUtil.isTypeJSONArray(value)) {
                this.values = JSONUtil.toList(JSONUtil.parseArray(value), String.class);
            } else {
                this.values = List.of(value);
            }
        }
    }
}
