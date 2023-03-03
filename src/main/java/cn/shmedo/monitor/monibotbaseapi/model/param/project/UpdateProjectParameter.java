package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.PredefinedModelProperTyCache;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbTagMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.util.PropertyUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProjectParameter implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "项目ID不允许为空")
    @Range(min = 1, message = "项目ID必须大于0")
    private Integer projectID;
    @NotBlank(message = "项目名称不允许为空")
    @Size(max = 50, message = "项目名称限制50个字符")
    private String projectName;
    @Size(max = 10, message = "项目简称限制10个字符")
    private String shortName;
    @Size(max = 50, message = "直管单位限制50个字符")
    @NotBlank(message = "直管单位不允许为空")
    private String directManageUnit;
    @NotNull(message = "项目启用字段不允许为空")
    private Boolean enable;
    @NotBlank(message = "四级行政区域信息不允许为空")
    @Size(max = 500, message = "四级行政区域信息限制500个字符")
    private String location;
    @NotBlank(message = "项目地址不允许为空")
    @Size(max = 100, message = "项目地址限制500个字符")
    private String projectAddress;
    @NotNull(message = "纬度不允许为空")
    private Double latitude;
    @NotNull(message = "经度不允许为空")
    private Double longitude;
    @Size(max = 1000, message = "项目描述限制1000个字符")
    private String projectDesc;
    private List<Integer> tagIDList;
    @Valid
    private List<@NotNull TagKeyAndValue> tagList;
    @Valid
    private List<PropertyIdAndValue> propertyList;

    @JsonIgnore
    private Integer companyID;
    @JsonIgnore
    private TbProjectInfo projectInfo;
    @JsonIgnore
    private List<TbProjectProperty> propertyDataList;

    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper projectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        projectInfo = projectInfoMapper.selectByPrimaryKey(projectID);
        if (projectInfo == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "找不到对应的工程项目");
        }
        companyID = projectInfo.getCompanyID();
        LambdaQueryWrapper<TbProjectInfo> wrapper = new LambdaQueryWrapper<TbProjectInfo>()
                .eq(TbProjectInfo::getID, projectID)
                .eq(TbProjectInfo::getProjectName, projectName);
        TbProjectInfo projectNameInfo = projectInfoMapper.selectOne(wrapper);
        if (projectNameInfo != null && !projectNameInfo.getID().equals(projectID)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "重复的项目名称");
        }
        if (ObjectUtil.isNotEmpty(propertyList)){
            List<TbProperty> properties = PredefinedModelProperTyCache.projectTypeAndPropertyListMap.get(projectInfo.getProjectType());
            if (projectInfo.getModelID() != null) {
                TbPropertyMapper tbPropertyMapper = ContextHolder.getBean(TbPropertyMapper.class);
                properties.addAll(tbPropertyMapper.queryByMID(projectInfo.getModelID()));
            }
            ResultWrapper temp = PropertyUtil.validPropertyValue(propertyList, properties, false);
            if (temp!=null){
                return temp;
            }
        }
        //校验标签
        TbTagMapper tbTagMapper = ContextHolder.getBean(TbTagMapper.class);
        if (ObjectUtil.isNotEmpty(tagIDList)) {
            if (tbTagMapper.countByCIDAndIDs(companyID, tagIDList) != tagIDList.size()) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "标签不存在或不属于本公司");
            }
        }
        // 总数不能大于5
        if ((ObjectUtil.isEmpty(tagIDList) ? 0 : tagIDList.size()) + (ObjectUtil.isEmpty(tagList) ? 0 : tagList.size()) > 5) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "标签数量不能大于5");
        }
        // 是否有重复的
        if (ObjectUtil.isNotEmpty(tagList)) {
            if (tagList.stream().map(
                    item -> item.getKey() + (ObjectUtil.isEmpty(item.getValue()) ? "_" : item.getKey())
            ).distinct().count() > 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "新增的标签中存在重复");
            }
            if (tbTagMapper.countByCIDAndTags(companyID, tagList) > 0) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有新增的标签已经存在");
            }

            int count = tbTagMapper.countByCIDAndIDs(companyID, null);
            if (count + tagList.size() > 100) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前公司下标签数量为：" + count + " ,新增会导致超过100");
            }
        }
        return null;
    }

    public TbProjectInfo buildProject(Integer userID) {
        projectInfo.setProjectName(projectName);
        projectInfo.setShortName(shortName);
        projectInfo.setDirectManageUnit(directManageUnit);
        projectInfo.setEnable(enable);
        projectInfo.setLocation(location);
        projectInfo.setProjectAddress(projectAddress);
        projectInfo.setLatitude(latitude);
        projectInfo.setLongitude(longitude);
        projectInfo.setProjectDesc(projectDesc);
        projectInfo.setUpdateTime(new Date());
        projectInfo.setUpdateUserID(userID);
        return projectInfo;
    }

    public List<TbProjectProperty> buildPropertyDataList() {
        propertyDataList.forEach(pd -> {
            propertyList.stream().filter(p -> p.getID().equals(pd.getPropertyID())).findFirst().ifPresent(p -> pd.setValue(p.getValue()));
        });
        return propertyDataList;
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
