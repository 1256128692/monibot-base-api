package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.PredefinedModelProperTyCache;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectRelationMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbTagMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectRelation;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.service.redis.RedisService;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    @NotNull
    @Range(min = -1, max = 2)
    private Byte level;
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

    private Integer newCompanyID;
    private Date newRetireDate;

    @Valid
    private List<@NotNull Integer> serviceIDList;

    @Size(max = 100)
    private String fileName;
    private String imageContent;
    private String imageSuffix;
    @JsonIgnore
    private Integer companyID;
    @JsonIgnore
    private TbProjectInfo projectInfo;
    @JsonIgnore
    private List<TbProjectProperty> propertyDataList;
    @JsonIgnore
    private List<TbProperty> properties;
    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper projectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        projectInfo = projectInfoMapper.selectById(projectID);
        if (projectInfo == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "找不到对应的工程项目");
        }
        if (projectInfoMapper.countByNameExcludeID(projectName,projectID) >0){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "名称已存在");
        }
        if (!projectInfo.getLevel().equals(level)) {
            TbProjectRelationMapper tbProjectRelationMapper = ContextHolder.getBean(TbProjectRelationMapper.class);
            if (tbProjectRelationMapper.selectCount(
                    new LambdaQueryWrapper<TbProjectRelation>()
                            .eq(TbProjectRelation::getUpLevelID, projectID)
                            .or()
                            .eq(TbProjectRelation::getDownLevelID, projectID)
            ) > 0) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该项目已经已经与其他项目关联，不允许修改级别");
            }
        }
        if (newCompanyID!=null){
            if (projectInfo.getCompanyID().equals(newCompanyID)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "目标公司与当前公司一样");
            }
        }
        if(newRetireDate != null){
            if (DateUtil.date().isAfter(newRetireDate)){
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "新有效期应当不小于今日");
            }
        }
        if (ObjectUtil.isNotEmpty(fileName)){
            if (fileName.contains(".")){
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "文件名称不能出现特殊字符,例如.");
            }
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
             properties = new ArrayList<>(PredefinedModelProperTyCache.projectTypeAndPropertyListMap.get(projectInfo.getProjectType()));
            if (projectInfo.getModelID() != null && !projectInfo.getModelID().equals(properties.get(0).getModelID())) {
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
            ).distinct().count() !=tagList.size()) {
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

        if (ObjectUtil.isNotEmpty(serviceIDList)) {
            RedisService redisService = ContextHolder.getBean(RedisService.class);
            List<Integer> allServiceIDLIst = redisService.hashKeys(DefaultConstant.REDIS_KEY_MD_AUTH_SERVICE).stream().map(Integer::valueOf).toList();
            if (serviceIDList.stream().anyMatch(item -> !allServiceIDLIst.contains(item))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有服务不存在");
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
        if (newRetireDate !=null){
            projectInfo.setExpiryDate(newRetireDate);
        }
        projectInfo.setLevel(level);
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
