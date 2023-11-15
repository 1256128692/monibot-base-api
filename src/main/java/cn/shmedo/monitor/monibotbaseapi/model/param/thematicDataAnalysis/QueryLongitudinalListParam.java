package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import cn.shmedo.monitor.monibotbaseapi.model.standard.WetLineBaseParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-13 13:25
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryLongitudinalListParam extends WetLineBaseParam {
    private CutoffWallConfig cutoffWallConfig;

    @Override
    public List<Integer> getInspectedPointIDList() {
        List<Integer> list = new ArrayList<>(getMonitorPointIDList());
        if (Objects.nonNull(cutoffWallConfig)) {
            list.addAll(cutoffWallConfig.getMonitorPointIDList());
        }
        return list;
    }
}
