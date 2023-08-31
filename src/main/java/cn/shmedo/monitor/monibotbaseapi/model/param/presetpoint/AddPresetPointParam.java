package cn.shmedo.monitor.monibotbaseapi.model.param.presetpoint;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbVideoPresetPoint;
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
        this.tbVideoPresetPoint = new TbVideoPresetPoint();
        this.tbVideoPresetPoint.setDeviceVideoID(getDeviceVideoID());
        this.tbVideoPresetPoint.setPresetPointName(presetPointName);
        this.tbVideoPresetPoint.setPresetPointIndex(presetPointIndex);
        return super.validate();
    }
}
