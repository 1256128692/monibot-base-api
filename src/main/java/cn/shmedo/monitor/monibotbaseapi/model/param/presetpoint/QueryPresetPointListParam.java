package cn.shmedo.monitor.monibotbaseapi.model.param.presetpoint;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoDeviceSourceMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoDevice;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

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
    @NotNull(message = "通道号不能为空")
    private Integer channelNo;
    @JsonIgnore
    private TbVideoDevice tbVideoDevice;

    @Override
    public ResultWrapper validate() {
        List<TbVideoDevice> tbVideoDeviceList = ContextHolder.getBean(TbVideoDeviceMapper.class)
                .selectList(new LambdaQueryWrapper<TbVideoDevice>().eq(TbVideoDevice::getID, videoDeviceID));
        if (CollUtil.isEmpty(tbVideoDeviceList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "视频设备不存在");
        }
        tbVideoDevice = tbVideoDeviceList.get(0);
        if(ContextHolder.getBean(TbVideoDeviceSourceMapper.class).selectByDeviceSerial(tbVideoDevice.getDeviceSerial()).stream().noneMatch(u -> channelNo.equals(u.getChannelNo()))){
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该视频设备没有通道号为" + channelNo + "的通道");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
