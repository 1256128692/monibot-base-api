package cn.shmedo.monitor.monibotbaseapi.util;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProperty;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbPropertyModel;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbTag;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CreateType;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.AddProjectParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.TagKeyAndValue;
import cn.shmedo.monitor.monibotbaseapi.model.param.property.AddModelParam;

import java.util.Date;
import java.util.List;
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
        obj.setCreateType(CreateType.Customized.getType());
        obj.setDesc(param.getDesc());
        return obj;
    }

    public static List<TbProperty> fromAddModelParam2TbPropertyList(AddModelParam param, Integer userID, Integer modelID) {
        return param.getModelPropertyList().stream().map(item ->{
            TbProperty obj = new TbProperty();
            obj.setProjectType(param.getProjectType());
            obj.setName(item.getName());
            obj.setType(item.getType());
            obj.setUnit(item.getUnit());
            obj.setRequired(item.getRequired());
            obj.setMultiSelect(item.getMultiSelect());
            obj.setEnumField(item.getEnumField());
            obj.setCreateType(CreateType.Customized.getType());
            obj.setClassName(item.getClassName());
            obj.setDisplayOrder(item.getDisplayOrder());
            obj.setExValue(item.getExValue());
            obj.setModelID(modelID);
            return obj;
        }).collect(Collectors.toList());

    }
}
