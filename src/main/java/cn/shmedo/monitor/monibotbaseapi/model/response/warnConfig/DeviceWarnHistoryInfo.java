package cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig;

import lombok.Data;

import java.util.*;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-23 14:41
 */
@Data
public class DeviceWarnHistoryInfo {
    private Date warnTime;
    private Date warnEndTime;
    // key: "time","value";
    private List<Map<String, Object>> fourGSignalList;
    private List<Map<String, Object>> extPowerVoltList;
}
