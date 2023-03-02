package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.shmedo.monitor.monibotbaseapi.model.Company;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbTag;
import cn.shmedo.monitor.monibotbaseapi.model.dto.PropertyDto;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PlatformType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ProjectInfo extends TbProjectInfo {

    /**
     * 平台类型名称
     */
    private String platformTypeName;

    /**
     * 项目类型名称
     */
    private String projectTypeName;

    /**
     * 项目主类型名称
     */
    private String projectMainTypeName;

    /**
     * 公司信息
     */
    private Company company;

    /**
     * 项目标签信息
     */
    private List<? extends TbTag> tagInfo;

    /**
     * 项目属性信息
     */
    private List<PropertyDto> propertyList;

    public static ProjectInfo create(TbProjectInfo info) {
        ProjectInfo result = new ProjectInfo();
        result.setID(info.getID());
        result.setCompanyID(info.getCompanyID());
        result.setProjectName(info.getProjectName());
        result.setShortName(info.getShortName());
        result.setProjectType(info.getProjectType());
        result.setExpiryDate(info.getExpiryDate());
        result.setDirectManageUnit(info.getDirectManageUnit());
        result.setPlatformType(info.getPlatformType());
        result.setEnable(info.getEnable());
        result.setLocation(info.getLocation());
        result.setLatitude(info.getLatitude());
        result.setLongitude(info.getLongitude());
        result.setImagePath(info.getImagePath());
        result.setProjectDesc(info.getProjectDesc());
        result.setModelID(info.getModelID());
        result.setProjectAddress(info.getProjectAddress());
        result.setCreateTime(info.getCreateTime());
        result.setCreateUserID(info.getCreateUserID());
        result.setUpdateTime(info.getUpdateTime());
        result.setUpdateUserID(info.getUpdateUserID());

        PlatformType platformType = PlatformType.getPlatformType(info.getPlatformType());
        if (platformType != null) {
            result.setPlatformTypeName(platformType.getTypeStr());
        }
        return result;
    }
}
