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
        // 产品说'浸润线'是'水位'的延申,所以浸润线支持的'显示密度'和'统计方式'就必然是水位支持的,所以这里没有额外对水位监测点做这两块的校验
        List<Integer> list = new ArrayList<>(getMonitorPointIDList());
        if (Objects.nonNull(cutoffWallConfig)) {
            list.addAll(cutoffWallConfig.getMonitorPointIDList());
        }
        return list;
    }
}
