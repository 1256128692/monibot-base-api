package cn.shmedo.monitor.monibotbaseapi.model.param.presetpoint;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-31 13:43
 */
@Data
public class QueryPresetPointListParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID不能小于1")
    private Integer companyID;
    @NotNull(message = "视频设备ID不能为空")
    @Positive(message = "视频设备ID不能小于1")
    private Integer videoDeviceID;

    @Override
    public ResultWrapper validate() {
        if (!ContextHolder.getBean(TbVideoDeviceMapper.class).exists(new LambdaQueryWrapper<TbVideoDevice>().eq(TbVideoDevice::getID, videoDeviceID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频设备不存在");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
