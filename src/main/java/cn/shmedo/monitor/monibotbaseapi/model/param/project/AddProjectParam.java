package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.PredefinedModelProperTyCache;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbTagMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PlatformType;
import cn.shmedo.monitor.monibotbaseapi.util.PropertyUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-22 13:28
 **/
public class AddProjectParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^[\\u4e00-\\u9fa5A-Za-z0-9]+$" , message = "只允许数字字母和中文")
    private String projectName;
    @Size(max = 10)
    private String shortName;
    @NotNull
    private Byte projectType;
    private String imageContent;
    private String imageSuffix;
    @NotNull
    private Date expiryDate;
    @Size(max = 50)
    @NotBlank
    private String directManageUnit;
    @NotNull
    private Byte platformType;
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
    private List<@NotNull Integer> monitorTypeList;
    private Integer modelID;
    @Valid
    @NotEmpty
    private List<@NotNull PropertyIdAndValue> modelValueList;
    @JsonIgnore
    List<TbProperty> properties;

    @Override
    public ResultWrapper validate() {

        if (!ProjectTypeCache.projectTypeMap.containsKey(projectType)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目类型不合法");
        }
        if (DateUtil.between(DateUtil.date(), expiryDate, DateUnit.DAY, false) <= 1) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有效日期不能小于1日");
        }
        if (!PlatformType.validate(platformType)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "平台类型不合法");
        }

        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        if (tbProjectInfoMapper.countByName(projectName) > 0) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该名称在系统中已存在");
        }
        if (modelID != null) {

            TbPropertyModelMapper tbPropertyModelMapper = ContextHolder.getBean(TbPropertyModelMapper.class);
            TbPropertyModel tbPropertyModel = tbPropertyModelMapper.selectByPrimaryKey(modelID);
            if (tbPropertyModel == null) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板不存在");
            }
            if (!tbPropertyModel.getProjectType().equals(projectType)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板与项目不适配");
            }
        }
        properties = new ArrayList<>(PredefinedModelProperTyCache.projectTypeAndPropertyListMap.get(projectType));
        if (ObjectUtil.isEmpty(properties)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "系统无此项目类型的预定义模板");
        }
        if (modelID != null) {
            TbPropertyMapper tbPropertyMapper = ContextHolder.getBean(TbPropertyMapper.class);
            properties.addAll(tbPropertyMapper.queryByMID(modelID));
        }
        ResultWrapper temp = PropertyUtil.validPropertyValue(modelValueList, properties, true);
        if (temp!=null){
            return temp;
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

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }


    public Integer getCompanyID() {
        return companyID;
    }

    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Byte getProjectType() {
        return projectType;
    }

    public void setProjectType(Byte projectType) {
        this.projectType = projectType;
    }

    public String getImageContent() {
        return imageContent;
    }

    public void setImageContent(String imageContent) {
        this.imageContent = imageContent;
    }

    public String getImageSuffix() {
        return imageSuffix;
    }

    public void setImageSuffix(String imageSuffix) {
        this.imageSuffix = imageSuffix;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getDirectManageUnit() {
        return directManageUnit;
    }

    public void setDirectManageUnit(String directManageUnit) {
        this.directManageUnit = directManageUnit;
    }

    public Byte getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Byte platformType) {
        this.platformType = platformType;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProjectAddress() {
        return projectAddress;
    }

    public void setProjectAddress(String projectAddress) {
        this.projectAddress = projectAddress;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getProjectDesc() {
        return projectDesc;
    }

    public void setProjectDesc(String projectDesc) {
        this.projectDesc = projectDesc;
    }

    public List<Integer> getTagIDList() {
        return tagIDList;
    }

    public void setTagIDList(List<Integer> tagIDList) {
        this.tagIDList = tagIDList;
    }

    public List<TagKeyAndValue> getTagList() {
        return tagList;
    }

    public void setTagList(List<TagKeyAndValue> tagList) {
        this.tagList = tagList;
    }

    public List<Integer> getMonitorTypeList() {
        return monitorTypeList;
    }

    public void setMonitorTypeList(List<Integer> monitorTypeList) {
        this.monitorTypeList = monitorTypeList;
    }

    public Integer getModelID() {
        return modelID;
    }

    public void setModelID(Integer modelID) {
        this.modelID = modelID;
    }

    public List<PropertyIdAndValue> getModelValueList() {
        return modelValueList;
    }

    public void setModelValueList(List<PropertyIdAndValue> modelValueList) {
        this.modelValueList = modelValueList;
    }

    public void setProperties(List<TbProperty> properties) {
        this.properties = properties;
    }

    public List<TbProperty> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "AddProjectParam{" +
                "companyID=" + companyID +
                ", projectName='" + projectName + '\'' +
                ", shortName='" + shortName + '\'' +
                ", projectType=" + projectType +
                ", imageContent='" + imageContent + '\'' +
                ", imageSuffix='" + imageSuffix + '\'' +
                ", expiryDate=" + expiryDate +
                ", directManageUnit='" + directManageUnit + '\'' +
                ", platformType=" + platformType +
                ", enable=" + enable +
                ", location='" + location + '\'' +
                ", projectAddress='" + projectAddress + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", projectDesc='" + projectDesc + '\'' +
                ", tagIDList=" + tagIDList +
                ", tagList=" + tagList +
                ", monitorTypeList=" + monitorTypeList +
                ", modelID=" + modelID +
                ", modelValueList=" + modelValueList +
                '}';
    }
}
