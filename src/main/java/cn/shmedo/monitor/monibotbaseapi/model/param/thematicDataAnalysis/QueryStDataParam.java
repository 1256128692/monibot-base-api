package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.hutool.core.collection.CollectionUtil;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroup;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.List;

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
    @JsonIgnore
    private TbMonitorGroup tbMonitorGroup;

    @Override
    public ResultWrapper validate() {
        List<TbMonitorGroup> tbMonitorGroups = ContextHolder.getBean(TbMonitorGroupMapper.class)
                .selectList(new LambdaQueryWrapper<TbMonitorGroup>().eq(TbMonitorGroup::getID, monitorGroupID));
        if (CollectionUtil.isEmpty(tbMonitorGroups)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测组不存在");
        }
        tbMonitorGroup = tbMonitorGroups.get(0);
        return super.validate();
    }
}
