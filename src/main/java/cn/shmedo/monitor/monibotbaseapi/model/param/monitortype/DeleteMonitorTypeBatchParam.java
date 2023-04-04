package cn.shmedo.monitor.monibotbaseapi.model.param.monitortype;

import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.Resource;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbSensorMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorType;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.validation.Valid;
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
 * @create: 2023-03-30 19:50
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteMonitorTypeBatchParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull
    private Integer companyID;
    @NotEmpty
    @Size(max = 10)
    @Valid
    private List<@NotNull Integer> monitorTypeList;
    @Override
    public ResultWrapper validate() {
        TbMonitorTypeMapper tbMonitorTypeMapper = ContextHolder.getBean(TbMonitorTypeMapper.class);
        List<TbMonitorType> list = tbMonitorTypeMapper.selectList(new QueryWrapper<TbMonitorType>().in("monitorType", monitorTypeList));
        if (list.stream().anyMatch(item -> item.getCompanyID().equals(-1) || !item.getCompanyID().equals(companyID))){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "模板对应的监测类型不是自定义或不属于该公司");
        }
        // 有传感器使用的不可删除
        TbSensorMapper tbSensorMapper = ContextHolder.getBean(TbSensorMapper.class);
        if (tbSensorMapper.selectCount(new QueryWrapper<TbSensor>().in("monitorType", monitorTypeList)) > 0){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有传感器使用的不可删除");
        }
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
