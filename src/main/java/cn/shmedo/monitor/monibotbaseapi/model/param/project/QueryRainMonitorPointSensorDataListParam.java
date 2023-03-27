package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.hutool.core.date.DateUtil;
import cn.shmedo.iot.entity.api.ParameterValidator;
import cn.shmedo.iot.entity.api.ResultCode;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;

@Data
public class QueryRainMonitorPointSensorDataListParam implements ParameterValidator {
    @NotNull
    private Integer projectID;

    @NotNull
    private Integer monitorPointID;

    @NotNull
    private Timestamp begin;

    @NotNull
    private Timestamp end;

    private String density;

    @JsonIgnore
    private TbMonitorPoint tbMonitorPoint;


    @Override
    public ResultWrapper<?> validate() {
        // 加校验(1.监测点的项目ID必须与项目ID一致 2.密度不为空是,必须以h或者d结尾)
        TbMonitorPointMapper tbMonitorPointMapper = ContextHolder.getBean(TbMonitorPointMapper.class);
        LambdaQueryWrapper<TbMonitorPoint> wrapper = new LambdaQueryWrapper<TbMonitorPoint>()
                .eq(TbMonitorPoint::getID, monitorPointID);
        this.tbMonitorPoint = tbMonitorPointMapper.selectOne(wrapper);

        if (tbMonitorPoint == null) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前监测点不存在");
        } else {
            if (!tbMonitorPoint.getProjectID().equals(projectID)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前监测点的项目ID与条件项目ID不符");
            }
            if (!tbMonitorPoint.getMonitorType().equals(MonitorType.RAINFALL.getKey())) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前监测点类型不为雨量类型");
            }
        }

        if (StringUtils.isNotBlank(density)) {
            if (!(density.endsWith("h") || density.endsWith("d"))) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前查询密度条件错误");
            }
        }

        // 将 begin 的时间减去 2 小时
        this.begin = new Timestamp(DateUtil.offsetHour(begin, -2).getTime());
        return null;
    }
}
