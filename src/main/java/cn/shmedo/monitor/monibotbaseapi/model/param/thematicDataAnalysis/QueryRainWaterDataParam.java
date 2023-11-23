package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDataEventMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbEigenValueMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis.ThematicEigenValueData;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-13 15:01
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryRainWaterDataParam extends QueryRainWaterDataBaseInfo {
    private List<Integer> eigenvalueIDList;
    private List<Integer> dataEventIDList;
    @JsonIgnore
    List<ThematicEigenValueData> eigenvalueDataList;
    @JsonIgnore
    List<Map<String, Object>> dataEventDataList;

    @Override
    public ResultWrapper validate() {
        ResultWrapper validate = super.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        List<Integer> monitorIDList = getMonitorIDList();
        List<Integer> monitorItemList = ContextHolder.getBean(TbMonitorPointMapper.class).selectList(new LambdaQueryWrapper<TbMonitorPoint>()
                .in(TbMonitorPoint::getID, monitorIDList)).stream().map(TbMonitorPoint::getMonitorItemID).distinct().toList();
        Optional.ofNullable(eigenvalueIDList).filter(CollUtil::isNotEmpty).map(u -> ContextHolder
                .getBean(TbEigenValueMapper.class).selectBaseInfoByIDList(u, monitorIDList)).ifPresent(this::setEigenvalueDataList);
        Optional.ofNullable(dataEventIDList).filter(CollUtil::isNotEmpty).map(u -> ContextHolder
                .getBean(TbDataEventMapper.class).selectBaseInfoByIDList(u, monitorItemList)).ifPresent(this::setDataEventDataList);
        //{@code eigenvalueIDList}/{@code dataEventIDList} won't be null cause associated {@code eigenvalueDataList}/{@code dataEventDataList} is not null
        if (Objects.nonNull(eigenvalueDataList) && eigenvalueDataList.size() != Objects.requireNonNull(eigenvalueIDList).size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有特征值不存在!");
        }
        if (Objects.nonNull(dataEventDataList) && dataEventDataList.size() != Objects.requireNonNull(dataEventIDList).size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有大事记不存在!");
        }
        return null;
    }
}
