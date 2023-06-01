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
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CreateType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PlatformType;
import cn.shmedo.monitor.monibotbaseapi.util.PropertyUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-22 13:28
 **/
@Data
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
    private List<@NotNull Integer> monitorItemIDList;
    private Integer modelID;
    @Valid
    @NotEmpty
    private List<@NotNull PropertyIdAndValue> modelValueList;
    @JsonIgnore
    List<TbProperty> properties;
    @JsonIgnore
    List<TbMonitorItem> monitorItems;

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
        if (tbProjectInfoMapper.countByNameExcludeID(projectName, null) > 0) {
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
        if (!PredefinedModelProperTyCache.projectTypeAndPropertyListMap.containsKey(projectType)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "系统无此项目类型的预定义模板");
        }
        properties = new ArrayList<>(PredefinedModelProperTyCache.projectTypeAndPropertyListMap.get(projectType));

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
        // 校验监测项目
        if (CollectionUtils.isNotEmpty(monitorItemIDList)) {
            TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
            monitorItems = tbMonitorItemMapper.selectBatchIds(monitorItemIDList);
            if (monitorItems.size() != monitorItemIDList.size()) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目不存在");
            }
            if (monitorItems.stream().anyMatch(item -> !((item.getCreateType().equals(CreateType.PREDEFINED.getType())) && (item.getCompanyID() == -1 || item.getProjectID() == -1)))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目应为预定义或公司定义模板");
            }
            if (monitorItems.stream().filter(item -> item.getCompanyID() != -1).anyMatch(item -> !item.getCompanyID().equals(companyID))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有监测项目不属于该公司模板");
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





}
