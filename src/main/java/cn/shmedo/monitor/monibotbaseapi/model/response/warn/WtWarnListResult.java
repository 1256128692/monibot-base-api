package cn.shmedo.monitor.monibotbaseapi.model.response.warn;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Chengfs on 2023/10/20
 */
@Data
public class WtWarnListResult {

    private Map<Integer, Long> statistic;
    private List<WtWarnLogBase> list;
}