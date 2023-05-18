package cn.shmedo.monitor.monibotbaseapi.model.param.third.iot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chengfs on 2023/5/17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryDeviceStateParam {

    /**
     * 设备ID, 与deviceToken二选一
     */
    private Integer deviceID;

    /**
     * 设备SN, 与deviceID二选一
     */
    private String deviceToken;
}