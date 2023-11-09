package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.hutool.json.JSONException;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorGroupMapper;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorGroupPointMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroup;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorGroupPoint;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IDataDisplayCheck;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import org.hibernate.validator.constraints.Range;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-09 14:35
 */
@Data
public class QueryTransverseListParam implements IDataDisplayCheck, ParameterValidator, ResourcePermissionProvider<Resource> {
    @Positive(message = "工程ID必须大于0")
    @NotNull(message = "工程ID不能为空")
    private Integer projectID;
    @Positive(message = "监测点组ID必须大于0")
    @NotNull(message = "监测点组ID不能为空")
    private Integer monitorGroupID;
    @NotEmpty(message = "监测点ID列表不能为空")
    private List<Integer> monitorPointIDList;
    @NotNull(message = "属性不能为空")
    @Range(min = 1, max = 2, message = "属性 1.管内水位高程 2.空管距离")
    private Integer queryDataType;
    @NotNull(message = "显示密度不能为空")
    @Range(max = 5, message = "显示密度 0.全部 1.小时 2.日 3.周 4.月 5.年")
    private Integer displayDensity;
    @NotNull(message = "统计方式不能为空")
    @Range(min = 1, max = 4, message = "统计方式 1.最新一条 2.平均值 3.阶段累计 4.阶段变化")
    private Integer statisticalMethod;
    @NotNull(message = "开始时间不能为空")
    private Date startTime;
    @NotNull(message = "结束时间不能为空")
    private Date endTime;
    private DatumPointConfig datumPoint;

    @Override
    public ResultWrapper<?> validate() {
        if (!ContextHolder.getBean(TbMonitorGroupMapper.class).exists(new LambdaQueryWrapper<TbMonitorGroup>()
                .eq(TbMonitorGroup::getID, monitorGroupID))) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测点组不存在!");
        }
        if (ContextHolder.getBean(TbMonitorGroupPointMapper.class).selectCount(new LambdaQueryWrapper<TbMonitorGroupPoint>()
                .eq(TbMonitorGroupPoint::getMonitorGroupID, monitorGroupID)
                .in(TbMonitorGroupPoint::getMonitorPointID, monitorPointIDList)) != monitorPointIDList.size()) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "有部分监测点不属于该监测点组!");
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

    @Override
    public Resource parameter() {
        return new Resource(projectID.toString(), ResourceType.BASE_PROJECT);
    }

    @Override
    public List<Integer> getInspectedPointIDList() {
        List<Integer> list = new ArrayList<>(monitorPointIDList);
        if (Objects.nonNull(datumPoint)) {
            list.add(datumPoint.getMonitorPointID());
        }
        return list;
    }
}
