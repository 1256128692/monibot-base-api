package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.shmedo.monitor.monibotbaseapi.model.standard.WetLineBaseParam;
import com.fasterxml.jackson.annotation.JsonIgnore;
import cn.shmedo.iot.entity.api.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.shmedo.monitor.monibotbaseapi.config.DefaultConstant.ThematicFieldToken.*;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-09 14:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryTransverseListParam extends WetLineBaseParam {
    @NotNull(message = "查询的属性不能为空")
    @Range(min = 1, max = 2, message = "属性 1.管内水位高程 2.空管距离")
    private Integer queryDataType;
    private DatumPointConfig datumPoint;
    @JsonIgnore
    private String fieldToken;

    @Override
    public ResultWrapper validate() {
        this.fieldToken = queryDataType == 1 ? LEVEL_ELEVATION : EMPTY_PIPE_DISTANCE;
        return super.validate();
    }

    @Override
    public List<Integer> getInspectedPointIDList() {
        List<Integer> list = new ArrayList<>(getMonitorPointIDList());
        if (Objects.nonNull(datumPoint)) {
            list.add(datumPoint.getMonitorPointID());
        }
        return list;
    }
}
