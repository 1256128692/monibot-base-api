package cn.shmedo.monitor.monibotbaseapi.model.response.monitorType;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class QueryFormulaParamsResult {
    /**
     * 物联网数据源参数信息
     */
    private Map<String, Object> iot;
    /**
     * 其他监测类型数据源参数信息
     */
    private Map<String, Object> mon;
    /**
     * 自身传感器参数信息
     */
    private List<String> selfList;
    /**
     * 公式参数信息
     */
    private List<String> paramList;
    /**
     * 拓展配置字段信息
     */
    private List<String> exList;
}
