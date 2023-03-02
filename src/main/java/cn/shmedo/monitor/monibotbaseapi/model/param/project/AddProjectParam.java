package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.cache.PredefinedModelProperTyCache;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbPropertyModelMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbTagMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PlatformType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PropertyType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private String projectName;
    @Size(max = 20)
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
    private List<@NotNull IDAndValue> modelValueList;
    @JsonIgnore
    List<TbProperty> properties;

    @Override
    public ResultWrapper validate() {

        if (ProjectTypeCache.projectTypeMap.get(projectType) == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目类型不合法");
        }
        if (DateUtil.between(DateUtil.date(), expiryDate, DateUnit.DAY, false) <= 1) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有效日期不能小于1日");
        }
        if (!PlatformType.validate(platformType)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "平台类型不合法");
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
        if (ObjectUtil.isNotEmpty(modelValueList)) {
            properties = PredefinedModelProperTyCache.projectTypeAndPropertyListMap.get(projectType);
            if (modelID != null) {
                TbPropertyMapper tbPropertyMapper = ContextHolder.getBean(TbPropertyMapper.class);
                properties.addAll(tbPropertyMapper.queryByMID(modelID));
            }
            Map<Integer, IDAndValue> idAndValueMap = modelValueList.stream().collect(Collectors.toMap(IDAndValue::getpID, Function.identity()));
            // 校验必填
            boolean b2 = properties.stream().filter(item ->!item.getRequired())
                    .anyMatch(item -> {
                        IDAndValue temp = idAndValueMap.get(item.getID());
                        if (temp == null ||  ObjectUtil.isEmpty(temp.getValue())){
                            System.err.println(temp.getpID() + temp.getValue());
                            return true;
                        }
                        return false;
                    });
            // 校验枚举
            boolean b1 = properties.stream().filter(item -> item.getType().equals(PropertyType.TYPE_ENUM.getType()))
                    .anyMatch(item -> {
                        JSONArray enums = JSONUtil.parseArray(item.getEnumField());
                        IDAndValue temp = idAndValueMap.get(item.getID());
                        if (temp!=null &&!enums.contains(temp.getValue())) {
                            System.err.println(temp.getpID() + temp.getValue());
                            return true;
                        }
                        return false;
                    });
            if (b1 || b2) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "枚举类型的属性值非法或必填项未填入");
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

    @Override
    public Resource parameter() {

        return null;
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionProvider.super.resourcePermissionType();
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

    public List<IDAndValue> getModelValueList() {
        return modelValueList;
    }

    public void setModelValueList(List<IDAndValue> modelValueList) {
        this.modelValueList = modelValueList;
    }

    public List<TbProperty> getProperties() {
        return properties;
    }
}
