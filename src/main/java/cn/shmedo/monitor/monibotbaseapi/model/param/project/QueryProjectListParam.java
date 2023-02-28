package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Author cyf
 * @Date 2023/2/23 10:17
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.param.project
 * @ClassName: QueryProjectListParam
 * @Description: TODO
 * @Version 1.0
 */
@Data
public class QueryProjectListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {

    private Integer pageSize;
    private Integer currentPage;
    private String projectName;
    private String directManageUnit;
    private String location;
    private Integer companyId;
    private Integer projectType;
    private Boolean enable;
    private List<Integer> platformTypeList;
    private Timestamp expiryDate;
    private Timestamp beginCreateTime;
    private Timestamp endCreateTime;
    private List<PropertyQueryEntity> propertyEntity;

    private String expiryDateStr;

    @Override
    public ResultWrapper validate() {
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
}
