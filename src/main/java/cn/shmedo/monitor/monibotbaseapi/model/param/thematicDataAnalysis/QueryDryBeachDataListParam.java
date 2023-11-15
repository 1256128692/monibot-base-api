package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import cn.shmedo.iot.entity.api.*;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.Date;
import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-13 17:57
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryDryBeachDataListParam extends QueryDryBeachDataParam {
    @NotNull(message = "显示密度不能为空")
    @Range(max = 6, message = "显示密度 1.全部 2.小时 3.日 4.周 5.月 6.年")
    private Integer displayDensity;
    @NotNull(message = "开始时间不能为空")
    private Date startTime;
    @NotNull(message = "结束时间不能为空")
    private Date endTime;

    @Override
    public ResultWrapper validate() {
        ResultWrapper validate = super.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        if (getDataList().stream().map(u -> u.get("exValues")).filter(ObjectUtil::isNotEmpty).map(JSONUtil::parseObj)
                .filter(u -> u.containsKey(DbConstant.DISPLAY_DENSITY)).map(u -> u.get(DbConstant.DISPLAY_DENSITY))
                .map(JSONUtil::parseArray).anyMatch(u -> !u.contains(displayDensity))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "存在监测点不支持选择的显示密度!");
        }
        return null;
    }
}
