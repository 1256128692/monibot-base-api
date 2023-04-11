package cn.shmedo.monitor.monibotbaseapi.util;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CreateType;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitorItem.AddMonitorItemParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.monitortype.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.AddProjectParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.TagKeyAndValue;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.AddModelParam;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 14:20
 **/
public class Param2DBEntityUtil {

    public static TbProjectInfo fromAddProjectParam2TbProjectInfo(AddProjectParam pa, Integer userID, String imgPath) {
        Date now = new Date();
        TbProjectInfo obj = new TbProjectInfo();
        obj.setCompanyID(pa.getCompanyID());
        obj.setProjectName(pa.getProjectName());
        obj.setShortName(pa.getShortName());
        obj.setProjectType(pa.getProjectType());
        obj.setExpiryDate(pa.getExpiryDate());
        obj.setDirectManageUnit(pa.getDirectManageUnit());
        obj.setPlatformType(pa.getPlatformType());
        obj.setEnable(pa.getEnable());
        obj.setLocation(pa.getLocation());
        obj.setProjectAddress(pa.getProjectAddress());
        obj.setLongitude(pa.getLongitude());
        obj.setLatitude(pa.getLatitude());
        obj.setImagePath(imgPath);
        obj.setProjectDesc(pa.getProjectDesc());
        obj.setModelID(pa.getModelID());
        obj.setCreateUserID(userID);
        obj.setUpdateUserID(userID);
        obj.setCreateTime(now);
        obj.setUpdateTime(now);
        return obj;
    }

    public static List<TbTag> from2TbTagList(List<TagKeyAndValue> tagList, Integer companyID, Integer userID) {
        Date now = new Date();
        return tagList.stream().map(
                item -> {
                    TbTag temp = new TbTag();
                    temp.setCompanyID(companyID);
                    temp.setTagKey(item.getKey());
                    temp.setTagValue(item.getValue());
                    temp.setCreateUserID(userID);
                    temp.setUpdateUserID(userID);
                    temp.setCreateTime(now);
                    temp.setUpdateTime(now);
                    return temp;
                }
        ).collect(Collectors.toList());
    }

    public static TbPropertyModel fromAddModelParam2TbPropertyModel(AddModelParam param, Integer userID) {
        TbPropertyModel obj = new TbPropertyModel();
        obj.setName(param.getModelName());
        obj.setProjectType(param.getProjectType());
        obj.setCreateType(CreateType.CUSTOMIZED.getType());
        obj.setDesc(param.getDesc());
        return obj;
    }

    public static List<TbProperty> fromAddModelParam2TbPropertyList(AddModelParam param, Integer userID, Integer modelID) {
        return param.getModelPropertyList().stream().map(item -> {
            TbProperty obj = new TbProperty();
            obj.setProjectType(param.getProjectType());
            obj.setName(item.getName());
            obj.setType(item.getType());
            obj.setUnit(item.getUnit());
            obj.setRequired(item.getRequired());
            obj.setMultiSelect(item.getMultiSelect());
            obj.setEnumField(item.getEnumField());
            obj.setCreateType(CreateType.CUSTOMIZED.getType());
            obj.setClassName(item.getClassName());
            obj.setDisplayOrder(item.getDisplayOrder());
            if (ObjectUtil.isEmpty(item.getExValue())) {
                obj.setExValue(null);
            } else {
                obj.setExValue(item.getExValue());
            }
            obj.setModelID(modelID);
            return obj;
        }).collect(Collectors.toList());

    }

    public static TbMonitorType fromAddCustomizedMonitorTypeParam2tbMonitorType(AddCustomizedMonitorTypeParam pa, Integer userID, Integer type) {
        TbMonitorType obj = new TbMonitorType();
        obj.setMonitorType(type);
        obj.setTypeName(pa.getTypeName());
        obj.setTypeAlias(StringUtils.isBlank(pa.getTypeAlias())?pa.getTypeName():pa.getTypeAlias());
        obj.setDisplayOrder(null);
        obj.setMultiSensor(pa.getMultiSensor());
        obj.setApiDataSource(pa.getApiDataSource());
        obj.setCreateType(CreateType.CUSTOMIZED.getType());
        obj.setCompanyID(pa.getCompanyID());
        obj.setExValues(pa.getExValues());
        obj.setMonitorTypeClass(pa.getMonitorTypeClass());
        return obj;
    }

    public static List<TbMonitorTypeField> buildTbMonitorTypeFieldList(List<MonitorTypeField4Param> list, Integer type) {
        return  list.stream().map(item -> {
            TbMonitorTypeField obj = new TbMonitorTypeField();
            obj.setMonitorType(type);
            obj.setFieldName(item.getFieldName());
            obj.setFieldToken(item.getFieldToken());
            obj.setFieldDataType(item.getFieldDataType());
            obj.setFieldClass(item.getFieldClass());
            obj.setFieldUnitID(item.getFieldUnitID());
            obj.setParentID(null);
            obj.setDisplayOrder(null);
            obj.setExValues(item.getExValues());
            obj.setCreateType(item.getCreateType());
            obj.setFieldDesc(item.getFieldDesc());
            return obj;
        }).collect(Collectors.toList());
    }

    public static TbMonitorTypeTemplate fromAddTemplateParam2TbMonitorTypeTemplate(AddTemplateParam pa, Integer userID) {
        TbMonitorTypeTemplate obj = new TbMonitorTypeTemplate();
        obj.setName(pa.getName());
        obj.setDataSourceComposeType(pa.getDataSourceComposeType());
        obj.setTemplateDataSourceID(UUID.randomUUID().toString());
        obj.setMonitorType(pa.getMonitorType());
        obj.setCalType(pa.getCalType());
        obj.setDisplayOrder(pa.getDisplayOrder());
        obj.setExValues(pa.getExValues());
        obj.setCreateType(pa.getCreateType());
        if (obj.getCreateType().equals(CreateType.CUSTOMIZED.getType())){
            obj.setCompanyID(pa.getCompanyID());
        }else {
            obj.setCompanyID(-1);
        }
        obj.setDefaultTemplate(pa.getDefaultTemplate());
        return obj;
    }

    public static TbTemplateScript buildTbMonitorTypeTemplate(Integer templateID, Integer monitorType, String script) {
        return TbTemplateScript.builder().templateID(templateID).script(script).monitorType(monitorType).build();
    }

    public static List<TbTemplateFormula> buildTbTemplateFormulaList(Integer templateID, Integer monitorType, List<FormulaItem> list) {
        return list.stream().map(item -> TbTemplateFormula.builder().templateID(templateID).monitorType(monitorType).fieldID(item.getFieldID()).displayFormula(item.getDisplayFormula()).formula(item.getFormula()).fieldCalOrder(item.getFieldCalOrder())
                 .build()).collect(Collectors.toList());
    }

    public static List<TbTemplateDataSource> fromAddTemplateParam2TbTemplateDataSourceList(String dataSourceID, AddTemplateParam pa) {
        return pa.getTokenList().stream().map(item ->TbTemplateDataSource.builder().templateDataSourceID(dataSourceID).dataSourceType(item.getDatasourceType()).templateDataSourceToken(item.getToken()).build()).collect(Collectors.toList());
    }

    public static List<TbParameter> fromSetParamParam2TbParameterList(SetParamParam pa) {
        return pa.getParamList().stream().map(
                item -> TbParameter.builder()
                        .subjectID(item.getSubjectID()).subjectType(pa.getSubjectType()).dataType(item.getDataType()).token(item.getToken())
                        .name(item.getName()).paValue(item.getPaValue()).paUnitID(item.getPaUnitID()).paDesc(item.getPaDesc())
                        .build()
        ).collect(Collectors.toList());
    }

    public static TbMonitorType fromAddPredefinedMonitorTypeParam2tbMonitorType(AddPredefinedMonitorTypeParam pa, Integer usrID, Integer type) {
        TbMonitorType obj = new TbMonitorType();
        obj.setMonitorType(type);
        obj.setTypeName(pa.getTypeName());
        obj.setTypeAlias(StringUtils.isBlank(pa.getTypeAlias())?pa.getTypeName():pa.getTypeAlias());
        obj.setDisplayOrder(null);
        obj.setMultiSensor(pa.getMultiSensor());
        obj.setApiDataSource(pa.getApiDataSource());
        obj.setCreateType(CreateType.PREDEFINED.getType());
        obj.setCompanyID(-1);
        obj.setExValues(pa.getExValues());
        obj.setMonitorTypeClass(pa.getMonitorTypeClass());
        return obj;
    }

    public static TbMonitorItem fromAddMonitorItemParam2TbMonitorItem(AddMonitorItemParam pa, Integer userID) {
        Date now = new Date();
        TbMonitorItem obj = new TbMonitorItem();
        if (pa.getCreateType().equals(CreateType.PREDEFINED.getType())){
            obj.setCompanyID(-1);
            obj.setProjectID(-1);
            obj.setProjectType(null);
        }else {
            obj.setCompanyID(pa.getTbProjectInfo().getCompanyID());
            obj.setProjectID(pa.getProjectID());
            obj.setProjectType(pa.getTbProjectInfo().getProjectType().intValue());
        }
        obj.setMonitorClass(null);
        obj.setMonitorType(pa.getMonitorType());
        obj.setName(pa.getMonitorItemName());
        obj.setAlias(obj.getName());
        obj.setCreateType(pa.getCreateType());
        obj.setExValue(pa.getExValue());
        obj.setDisplayOrder(pa.getDisplayOrder());
        obj.setCreateTime(now);
        obj.setUpdateTime(now);
        obj.setCreateUserID(userID);
        obj.setUpdateUserID(userID);
        return obj;
    }
}
