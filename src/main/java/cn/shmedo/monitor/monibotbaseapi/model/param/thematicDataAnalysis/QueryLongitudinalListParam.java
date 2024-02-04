package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.shmedo.iot.entity.api.ResultWrapper;
import cn.shmedo.monitor.monibotbaseapi.model.standard.WetLineBaseParam;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-13 13:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryLongitudinalListParam extends WetLineBaseParam {
    @Valid
    private CutoffWallConfig cutoffWallConfig;
    @JsonIgnore
    private Map<Integer, Integer> orderMap;

    @Override
    public ResultWrapper validate() {
        ResultWrapper<?> superValidate = super.validate();
        if (Objects.nonNull(superValidate)) {
            return superValidate;
        }
        List<Integer> monitorPointIDList = getMonitorPointIDList();
        orderMap = IntStream.range(0, monitorPointIDList.size()).boxed().collect(Collectors.toMap(monitorPointIDList::get, Function.identity()));
        return null;
    }

    @Override
    public List<Integer> getInspectedPointIDList() {
        List<Integer> list = new ArrayList<>(getMonitorPointIDList());
        if (Objects.nonNull(cutoffWallConfig)) {
            list.addAll(cutoffWallConfig.getMonitorPointIDList());
        }
        return list;
    }
}
