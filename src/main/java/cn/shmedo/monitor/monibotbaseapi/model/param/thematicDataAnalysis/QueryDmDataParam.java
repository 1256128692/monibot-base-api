package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.iot.base.FieldSelectInfo;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.config.DbConstant;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorPointMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorTypeFieldMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorPoint;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorTypeField;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-18 18:31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryDmDataParam extends QueryThematicDataParam {
    @NotNull(message = "监测点ID不能为空")
    @Min(value = 1, message = "监测点ID不能小于1")
    private Integer monitorPointID;
    private String fieldToken;
    @JsonIgnore
    private TbMonitorPoint tbMonitorPoint;
    @JsonIgnore
    private List<FieldSelectInfo> fieldSelectInfoList;

    @Override
    public ResultWrapper validate() {
        List<TbMonitorPoint> tbMonitorPoints = ContextHolder.getBean(TbMonitorPointMapper.class)
                .selectList(new LambdaQueryWrapper<TbMonitorPoint>().eq(TbMonitorPoint::getID, monitorPointID));
        if (!CollectionUtil.isEmpty(tbMonitorPoints)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点不存在");
        }
        // 如果三轴位移的token永远不变的话,可以把这里的sql改成枚举硬编码
        List<String> fieldTokenList = ContextHolder.getBean(TbMonitorTypeFieldMapper.class)
                .selectListByMonitorID(monitorPointID).stream().sorted(Comparator.comparingInt(TbMonitorTypeField::getID))
                .map(TbMonitorTypeField::getFieldToken).toList();
        if (CollectionUtil.isEmpty(fieldTokenList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "该监测点所属监测项目尚未配置监测属性");
        }
        if (Objects.isNull(fieldToken)) {
            fieldToken = fieldTokenList.get(0);
        } else if (!fieldTokenList.contains(fieldToken)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "数据轴枚举不合法");
        }
        List<String> collect = fieldTokenList.stream().filter(u -> u.equals(fieldToken)).collect(Collectors.toList());
        collect.add(DbConstant.TIME_FIELD);
        collect.add(DbConstant.SENSOR_ID_TAG);
        fieldSelectInfoList = collect.stream().map(u -> {
            FieldSelectInfo info = new FieldSelectInfo();
            info.setFieldToken(u);
            return info;
        }).toList();
        tbMonitorPoint = tbMonitorPoints.get(0);
        return super.validate();
    }
}
