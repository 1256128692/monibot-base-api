package cn.shmedo.monitor.monibotbaseapi.model.param.presetpoint;

import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbVideoPresetPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoPresetPoint;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-08-31 13:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AddPresetPointParam extends QueryPresetPointListParam {
    @NotEmpty(message = "预置点名称不能为空")
    private String presetPointName;
    @NotNull(message = "预置点位置不能为空")
    private Integer presetPointIndex;
    @JsonIgnore
    private TbVideoPresetPoint tbVideoPresetPoint;

    @Override
    public ResultWrapper validate() {
        if (ContextHolder.getBean(TbVideoPresetPointMapper.class).exists(new LambdaQueryWrapper<TbVideoPresetPoint>()
                .eq(TbVideoPresetPoint::getVideoDeviceID, getVideoDeviceID()).eq(TbVideoPresetPoint::getChannelNo, getChannelNo())
                .eq(TbVideoPresetPoint::getPresetPointIndex, presetPointIndex))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该预置点已被添加");
        }
        this.tbVideoPresetPoint = new TbVideoPresetPoint();
        this.tbVideoPresetPoint.setVideoDeviceID(getVideoDeviceID());
        this.tbVideoPresetPoint.setChannelNo(getChannelNo());
        this.tbVideoPresetPoint.setPresetPointName(presetPointName);
        this.tbVideoPresetPoint.setPresetPointIndex(presetPointIndex);
        return super.validate();
    }
}
