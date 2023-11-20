package cn.shmedo.monitor.monibotbaseapi.model.standard;

import cn.hutool.json.JSONException;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.monitor.monibotbaseapi.model.db.*;
import cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis.QueryWetLineConfigParam;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.*;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-13 14:18
 */

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class WetLineBaseParam extends QueryWetLineConfigParam implements IDataDisplayCheck {
    @NotNull(message = "开始时间不能为空")
    private Date startTime;
    @NotNull(message = "结束时间不能为空")
    private Date endTime;
    @NotNull(message = "显示密度不能为空")
    @Range(min = 1, max = 6, message = "显示密度 1.全部 2.小时 3.日 4.周 5.月 6.年")
    private Integer displayDensity;
    @NotNull(message = "统计方式不能为空")
    @Range(min = 1, max = 4, message = "统计方式 1.最新一条 2.平均值 3.阶段累计 4.阶段变化")
    private Integer statisticalMethod;
    @JsonIgnore
    private List<TbMonitorPoint> tbMonitorPointList;

    @Override
    public ResultWrapper validate() {
        ResultWrapper<?> superValidate = super.validate();
        if (Objects.nonNull(superValidate)) {
            return superValidate;
        }
        try {
            if (!valid()) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "存在监测点不支持选择的显示密度或统计方式!");
            }
        } catch (JSONException e) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测类型配置的显示密度、统计方式错误!");
        } catch (IllegalArgumentException e) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有部分监测点不存在!");
        }
        return null;
    }
}
