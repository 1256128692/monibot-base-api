package cn.shmedo.monitor.monibotbaseapi.model.response;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.cache.ProjectTypeCache;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbTag;
import cn.shmedo.monitor.monibotbaseapi.model.dto.Company;
import cn.shmedo.monitor.monibotbaseapi.model.dto.PropertyDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Objects;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectInfo extends TbProjectInfo {

    /**
     * 项目有效期
     */
    private Date expiryDate;

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

    private String locationInfo;

    private List<AuthService> serviceList;


    private List<ProjectInfo> downLevelProjectList;

    @Override
    public void setProjectType(Byte projectType) {
        TbProjectType type = ProjectTypeCache.projectTypeMap.getOrDefault(projectType, null);
        if(type != null) {
            this.projectTypeName = type.getTypeName();
            this.projectMainTypeName = type.getMainType();
        }
        super.setProjectType(projectType);
    }

    @Override
    public void setLocation(String location) {
        super.setLocation(location);
        if (JSONUtil.isTypeJSON(location)) {
            JSONObject json = JSONUtil.parseObj(location);
            this.setLocationInfo(json.isEmpty() ? null : CollUtil.getLast(json.values()).toString());
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ProjectInfo that = (ProjectInfo) o;
        return Objects.equals(super.getID(), that.getID());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
