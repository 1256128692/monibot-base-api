package cn.shmedo.monitor.monibotbaseapi.model.param.presetpoint;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoPresetPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.response.presetPoint.PresetPointWithDeviceInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-09-25 13:44
 */
@Data
public class MovePresetPointParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID不能小于1")
    private Integer companyID;
    @NotNull(message = "预置点ID不能为空")
    private Integer presetPointID;
    @JsonIgnore
    private PresetPointWithDeviceInfo presetPointWithDeviceInfo;

    @Override
    public ResultWrapper validate() {
        List<PresetPointWithDeviceInfo> presetPointWithDeviceInfos = ContextHolder.getBean(TbVideoPresetPointMapper.class)
                .selectPresetPointWithDeviceInfo(List.of(presetPointID));
        if (CollUtil.isEmpty(presetPointWithDeviceInfos)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "预置点不存在");
        }
        presetPointWithDeviceInfo = presetPointWithDeviceInfos.get(0);
        if (Objects.isNull(presetPointWithDeviceInfo.getVideoDeviceID())) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "预置点关联的视频设备不存在");
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
