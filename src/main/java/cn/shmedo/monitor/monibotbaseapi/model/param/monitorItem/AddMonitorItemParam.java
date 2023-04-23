package cn.shmedo.monitor.monibotbaseapi.model.param.monitorItem;

import cn.hutool.json.JSONUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbProjectInfoMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbProjectInfo;
import cn.shmedo.monitor.monibotbaseapi.model.enums.CreateType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-10 14:20
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMonitorItemParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer projectID;
    @NotNull
    private Integer monitorType;
    @NotNull
    private Byte createType;
    @NotNull
    private Boolean enable;
    @NotBlank
    @Size(max = 20)
    private String monitorItemName;
    @Size(max = 500)
    private String exValue;
    private Integer displayOrder;
    @NotEmpty
    @Valid
    private List<@NotNull Integer> fieldIDList;

    @JsonIgnore
    private TbProjectInfo tbProjectInfo;

    @Override
    public ResultWrapper validate() {
        if (!CreateType.isValid(createType)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "创建类型不合法");
        }
        TbProjectInfoMapper tbProjectInfoMapper = ContextHolder.getBean(TbProjectInfoMapper.class);
        tbProjectInfo = tbProjectInfoMapper.selectByPrimaryKey(projectID);
        if (tbProjectInfo == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "工程项目不存在");
        }
        TbMonitorTypeMapper tbMonitorTypeMapper = ContextHolder.getBean(TbMonitorTypeMapper.class);
        TbMonitorType tbMonitorType = tbMonitorTypeMapper.queryByType(monitorType);
        if (tbMonitorType == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型不存在");
        }
        if (createType.equals(CreateType.PREDEFINED.getType()) && tbMonitorType.getCreateType().equals(CreateType.CUSTOMIZED.getType())) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "预定义监测项目，不能使用自定义的监测类型");
        }
        TbMonitorTypeFieldMapper tbMonitorTypeFieldMapper = ContextHolder.getBean(TbMonitorTypeFieldMapper.class);
        if (tbMonitorTypeFieldMapper.selectCount(
                new QueryWrapper<TbMonitorTypeField>().eq("monitorType", monitorType).in("id", fieldIDList)
        ) != fieldIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型属性有不属于该监测类型的");
        }
        if (StringUtils.isNotBlank(exValue) && JSONUtil.isTypeJSON(exValue)){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测项目额外属性不合法");
        }
        TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
        if (tbMonitorItemMapper.selectCount(
                new QueryWrapper<TbMonitorItem>().eq("projectID", projectID).eq("Name", monitorItemName)
        )>0){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该项目下监测项目名称已存在");
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
