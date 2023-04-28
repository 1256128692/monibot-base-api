package cn.shmedo.monitor.monibotbaseapi.model.response.report;

import lombok.Builder;
import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-27 13:18
 */
@Data
@Builder(toBuilder = true)
public class WtReportWarn {
    private Integer warnLevel;
    private Integer total;
    private String warnName;
}
