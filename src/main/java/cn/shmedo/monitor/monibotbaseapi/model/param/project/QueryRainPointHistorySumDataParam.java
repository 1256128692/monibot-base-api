package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import cn.shmedo.iot.entity.api.*;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionProvider;
import cn.shmedo.iot.entity.api.permission.ResourcePermissionType;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbMonitorItemMapper;
import cn.shmedo.monitor.monibotbaseapi.model.db.TbMonitorItem;
import cn.shmedo.monitor.monibotbaseapi.model.enums.AvgDensityType;
import cn.shmedo.monitor.monibotbaseapi.model.enums.RainDensityType;
import cn.shmedo.monitor.monibotbaseapi.util.TimeUtil;
import cn.shmedo.monitor.monibotbaseapi.util.base.CollectionUtil;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class QueryRainPointHistorySumDataParam  implements ParameterValidator, ResourcePermissionProvider<List<Resource>> {

    @NotEmpty(message = "工程ID列表不能为空")
    private List<Integer> projectIDList;

    @NotEmpty(message = "监测点ID列表不能为空")
    private List<Integer> monitorPointIDList;

    @NotNull(message = "开始时间不能为空")
    private Timestamp begin;

    @NotNull(message = "结束时间不能为空")
    private Timestamp end;

    @NotNull(message = "密度不能为空")
    private Integer density;


    @Override
    public ResultWrapper<?> validate() {
        // 校验监测点的监测项目名称,如果监测项目名称有1个以上,则这是跨监测项目,返回相应错误
        TbMonitorItemMapper tbMonitorItemMapper = ContextHolder.getBean(TbMonitorItemMapper.class);
        List<TbMonitorItem> monitorItemList = tbMonitorItemMapper.selectListByMonitorPointIDsAndProjectIDs(monitorPointIDList, projectIDList);
        if (CollectionUtil.isNullOrEmpty(monitorItemList)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前监测点列表没有对应的监测项目");
        } else {
            long count = monitorItemList.stream().map(TbMonitorItem::getName).distinct().count();
            if (count > 1) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "当前监测点列表所属不同监测项目");
            }
        }

        boolean validDensity = RainDensityType.isValidDensity(density);
        if (!validDensity) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "监测密度参数错误");
        }

        if (begin.after(end)) {
            return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "开始时间不能小于结束时间");
        }

        // 密度不为全部时,需要加入时间校验
        if (density != AvgDensityType.ALL.getValue()) {
            if (TimeUtil.validateTime(begin, end, density)) {
                return ResultWrapper.withCode(ResultCode.INVALID_PARAMETER, "密度为(日,月,年)时,开始或者结束时间分别不得为(当日,当月,当年)");
            }
        }
        return null;
    }


    @Override
    public List<Resource> parameter() {

        Set<Resource> collect = projectIDList.stream().map(item -> {
            if (item != null) {
                return new Resource(item.toString(), ResourceType.BASE_PROJECT);
            }
            return null;
        }).collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(item -> ResourceType.BASE_PROJECT + item.toString()))));

        return new ArrayList<>(collect);
    }

    @Override
    public ResourcePermissionType resourcePermissionType() {
        return ResourcePermissionType.BATCH_RESOURCE_SINGLE_PERMISSION;
    }

}
