package cn.shmedo.monitor.monibotbaseapi.model.param.presetpoint;

import cn.hutool.core.util.ObjectUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoPresetPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.response.presetPoint.PresetPointWithDeviceInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-31 13:25
 */
@Data
public class DeletePresetPointParam implements ParameterValidator, ResourcePermissionProvider<Resource> {
    @NotNull(message = "公司ID不能为空")
    @Positive(message = "公司ID不能小于1")
    private Integer companyID;
    @NotEmpty(message = "预置点IDList不能为空")
    private List<Integer> presetPointIDList;
    @JsonIgnore
    private List<PresetPointWithDeviceInfo> tbPresetPointWithDeviceInfoList;

    @Override
    public ResultWrapper validate() {
        this.tbPresetPointWithDeviceInfoList = ContextHolder.getBean(TbVideoPresetPointMapper.class)
                .selectPresetPointWithDeviceInfo(presetPointIDList);
        if (presetPointIDList.size() == 1) {
            if (this.tbPresetPointWithDeviceInfoList.size() < 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "要删除的预置点不存在!");
            } else if (ObjectUtil.isEmpty(this.tbPresetPointWithDeviceInfoList.get(0).getDeviceSerial())) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "要删除的预置点的设备不存在!");
            }
        }
        return null;
    }

    @Override
    public Resource parameter() {
        return new Resource(companyID.toString(), ResourceType.COMPANY);
    }
}
