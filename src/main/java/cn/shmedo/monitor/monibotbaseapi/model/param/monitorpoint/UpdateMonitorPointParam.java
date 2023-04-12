package cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-12 14:22
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMonitorPointParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer projectID;
    @NotNull
    private Integer pointID;
    @NotBlank
    @Size(max = 50)
    private String name;
    @NotNull
    private Boolean enable;
    @Size(max = 100)
    private String gpsLocation;
    @Size(max = 100)
    private String imageLocation;
    @Size(max = 100)
    private String overallViewLocation;
    @Size(max = 100)
    private String spatialLocation;
    @Size(max = 500)
    private String exValues;
    private Integer displayOrder;
    @JsonIgnore
    private TbMonitorPoint tbMonitorPoint;
    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        if (tbProjectInfoMapper.selectByPrimaryKey(projectID) == null){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
        }
        TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
        tbMonitorPoint =  tbMonitorPointMapper.selectByPrimaryKey(pointID);
        if (tbMonitorPoint == null){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点不存在");
        }
        return null;
    }
    public TbMonitorPoint update( Integer userID){
        tbMonitorPoint.setName(name);
        tbMonitorPoint.setEnable(enable);
        tbMonitorPoint.setGpsLocation(gpsLocation);
        tbMonitorPoint.setImageLocation(imageLocation);
        tbMonitorPoint.setOverallViewLocation(overallViewLocation);
        tbMonitorPoint.setSpatialLocation(spatialLocation);
        tbMonitorPoint.setExValues(exValues);
        tbMonitorPoint.setDisplayOrder(displayOrder);
        tbMonitorPoint.setUpdateTime(new Date());
        tbMonitorPoint.setUpdateUserID(userID);
        return tbMonitorPoint;
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
