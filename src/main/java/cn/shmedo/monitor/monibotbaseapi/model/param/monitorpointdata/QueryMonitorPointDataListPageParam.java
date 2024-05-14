package cn.shmedo.monitor.monibotbaseapi.model.param.monitorpointdata;

import cn.hutool.core.collection.CollUtil;
import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.config.ContextHolder;
import cn.shmedo.monitor.monibotbaseapi.dal.mapper.TbDataEventMapper;
import cn.shmedo.monitor.monibotbaseapi.model.response.dataEvent.QueryDataEventInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.List;
import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-04-28 09:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryMonitorPointDataListPageParam extends QueryMonitorPointDataParam {
    @NotNull(message = "页大小不能为空")
    @Range(min = 1, max = 100, message = "页大小 1~100")
    private Integer pageSize;
    @NotNull(message = "当前页不能为空")
    @Positive(message = "当前页不能小于1")
    private Integer currentPage;

    @Override
    public ResultWrapper<?> validate() {
        ResultWrapper<?> validate = super.validate();
        if (Objects.nonNull(validate)) {
            return validate;
        }
        if (CollUtil.isEmpty(getEventIDList())) {
            // 图表可以选择展示哪些大事记,列表默认展示全部
            List<Integer> eventIDList = ContextHolder.getBean(TbDataEventMapper.class).selectListByProjectIDAndItemIDs(
                    getProjectID(), List.of(getMonitorItemID())).stream().map(QueryDataEventInfo::getId).toList();
            setEventIDList(eventIDList);
        }
        return null;
    }
}
