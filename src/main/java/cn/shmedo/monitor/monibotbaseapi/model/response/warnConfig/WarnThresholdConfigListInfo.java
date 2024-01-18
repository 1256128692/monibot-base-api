package cn.shmedo.monitor.monibotbaseapi.model.response.warnConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-18 11:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WarnThresholdConfigListInfo extends WarnBaseConfigInfo {
    private List<WarnThresholdMonitorPointInfo> dataList;
}
