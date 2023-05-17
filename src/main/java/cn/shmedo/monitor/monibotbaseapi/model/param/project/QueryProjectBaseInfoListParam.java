package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.hutool.core.lang.Assert;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResourceType;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.enums.PlatformType;
import cn.shmedo.monitor.monibotbaseapi.util.PermissionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collection;
import java.util.Optional;

@Data
public class QueryProjectBaseInfoListParam  implements ParameterValidator, ResourcePermissionProvider<Resource> {


    @NotNull
    private Integer companyID;

    private String monitorItemName;

    @JsonIgnore
    private Collection<Integer> projectIDs;


    @Override
    public ResultWrapper<?> validate() {

        // 获取有权限的项目列表
        this.projectIDs = PermissionUtil.getHavePermissionProjectList(companyID);
        if (projectIDs.isEmpty()) {
            return ResultWrapper.successWithNothing();
        }

        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
        TbSensorMapper tbSensorMapper = ContextHolder.getBean(TbSensorMapper.class);
        // TODO:根据有权限的工程ID,查询工程信息列表

        // TODO:根据有权限的工程ID和监测项目名称,去查询监测点列表

        // TODO:根据监测点ID,去查询传感器列表

        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }

}
