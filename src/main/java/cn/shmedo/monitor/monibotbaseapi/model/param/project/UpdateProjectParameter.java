package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbTagMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProjectParameter implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer projectID;
    @NotBlank
    @Size(max = 50)
    private String projectName;
    @Size(max = 20)
    private String shortName;
    @Size(max = 50)
    @NotBlank
    private String directManageUnit;
    @NotNull
    private Boolean enable;
    @NotBlank
    @Size(max = 500)
    private String location;
    @NotBlank
    @Size(max = 100)
    private String projectAddress;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
    @Size(max = 2000)
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
        if (!CollectionUtil.isEmpty(propertyList)) {
            List<Integer> propertyIdList = propertyList.stream().map(PropertyIdAndValue::getPropertyID).collect(Collectors.toList());
            TbProjectPropertyMapper projectPropertyMapper = ContextHolder.getBean(TbProjectPropertyMapper.class);
            LambdaQueryWrapper<TbProjectProperty> propertyLambdaQueryWrapper = new LambdaQueryWrapper<TbProjectProperty>()
                    .in(TbProjectProperty::getPropertyID, propertyIdList)
                    .eq(TbProjectProperty::getProjectID, projectID);
            propertyDataList = projectPropertyMapper.selectList(propertyLambdaQueryWrapper);
            if (CollectionUtil.isEmpty(propertyDataList) || propertyDataList.size() != propertyIdList.size()) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "属性值列表含非法的属性");
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
            if (count + tagList.size() > 100){
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
            propertyList.stream().filter(p -> p.getPropertyID().equals(pd.getID())).findFirst().ifPresent(p -> pd.setValue(p.getValue()));
        });
        return propertyDataList;
    }

    @Override
    public Resource parameter() {
        return null;
    }
}
