package cn.shmedo.monitor.monibotbaseapi.model.response.monitorType;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder(toBuilder = true)
public class BaseFormulaParam {
    /**
     * 参数名称列表
     */
    private List<String> nameList;
    /**
     * 参数子类型列表
     */
    private Map<String, List<String>> childList;
}
