package cn.shmedo.monitor.monibotbaseapi.model.param.third.iot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2024-01-23 14:58
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryDeviceStateListParam {
    /**
     * 设备ID
     */
    private Integer deviceID;
    /**
     * 设备SN
     */
    private String deviceToken;
    /**
     * 开始时间
     */
    private Date begin;
    /**
     * 结束时间
     */
    private Date end;
}
