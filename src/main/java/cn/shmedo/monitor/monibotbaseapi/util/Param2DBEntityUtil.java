package cn.shmedo.monitor.monibotbaseapi.util;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbTag;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.AddProjectParam;
import cn.shmedo.monitor.monibotbaseapi.model.param.project.TagKeyAndValue;

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
}
