package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroup;
import cn.shmedo.monitor.monibotbaseapi.model.dto.thematicDataAnalysis.StRelateRuleDto;
import cn.shmedo.monitor.monibotbaseapi.model.enums.MonitorType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-16 18:34
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryStDataParam extends QueryThematicDataParam {
    @NotNull(message = "监测组ID不能为空")
    @Min(value = 1, message = "监测组ID不能小于1")
    private Integer monitorGroupID;
    @Range(min = 1, max = 3, message = "密度枚举错误,密度 1.日平均;2.月平均;3.年平均")
    private Integer density;
    @NotEmpty(message = "浸润线规则警戒值的upperName不能为空")
    private String upperName;
    @JsonIgnore
    private TbMonitorGroup tbMonitorGroup;
    @JsonIgnore
    private Map<Integer, Double> monitorPointIDUpperLimitMap;
    @JsonIgnore
    private List<FieldSelectInfo> fieldSelectInfoList;

    @Override
    public ResultWrapper validate() {
        TbMonitorGroupMapper tbMonitorGroupMapper = ContextHolder.getBean(TbMonitorGroupMapper.class);
        List<TbMonitorGroup> tbMonitorGroups = tbMonitorGroupMapper.selectList(new LambdaQueryWrapper<TbMonitorGroup>()
                .eq(TbMonitorGroup::getID, monitorGroupID));
        if (CollectionUtil.isEmpty(tbMonitorGroups)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测组不存在");
        }
        tbMonitorGroup = tbMonitorGroups.get(0);
        monitorPointIDUpperLimitMap = tbMonitorGroupMapper.selectWettingLineGroupUpperLimit(
                monitorGroupID, MonitorType.WATER_LEVEL.getKey(), upperName).stream().collect(Collectors.groupingBy(
                StRelateRuleDto::getMonitorPointID)).values().stream().map(u -> u.stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Unreachable Exception"))).collect(Collectors
                .toMap(StRelateRuleDto::getMonitorPointID, StRelateRuleDto::getUpperLimit));
        fieldSelectInfoList = Stream.of("distance", DbConstant.TIME_FIELD, DbConstant.SENSOR_ID_TAG).map(u -> {
            FieldSelectInfo info = new FieldSelectInfo();
            info.setFieldToken(u);
            return info;
        }).toList();
        return super.validate();
    }
}
