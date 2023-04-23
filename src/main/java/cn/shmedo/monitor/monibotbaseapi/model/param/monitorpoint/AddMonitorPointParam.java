package cn.shmedo.monitor.monibotbaseapi.model.param.monitorpoint;

import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-12 13:51
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMonitorPointParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer projectID;
    @NotNull
    private Integer monitorType;
    @NotNull
    private Integer monitorItemID;
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

    @Override
    public ResultWrapper validate() {
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        if (tbProjectInfoMapper.selectByPrimaryKey(projectID) == null){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目不存在");
        }
        TbMonitorTypeMapper tbMonitorTypeMapper = ContextHolder.getBean(TbMonitorTypeMapper.class);
        TbMonitorType tbMonitorType = tbMonitorTypeMapper.queryByType(monitorType);
        if (tbMonitorType == null){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型不存在");
        }
        TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
        TbMonitorItem tbMonitorItem = tbMonitorItemMapper.selectByPrimaryKey(monitorItemID);
        if (tbMonitorItem == null){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目不存在");
        }
        if (!tbMonitorItem.getProjectID().equals(projectID)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目不属于该工程项目");

        }
        if (!tbMonitorItem.getMonitorType().equals(monitorType)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目不属于该监测类型");

        }
        TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);

        if (tbMonitorPointMapper.selectCount(
                new QueryWrapper<TbMonitorPoint>().eq("projectID", projectID).eq("Name", name)
        )>0){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "项目下监测点名称已存在");
        }
        // TODO 4个位置的校验

        if (StringUtils.isNotBlank(exValues) && JSONUtil.isTypeJSON(exValues)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "额外属性不合法");
        }
        return null;
    }

    @Override
    public Resource parameter() {
      return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.SINGLE_RESOURCE_SINGLE_PERMISSION;
    }
}
