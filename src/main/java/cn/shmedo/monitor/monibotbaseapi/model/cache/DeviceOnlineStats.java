package cn.shmedo.monitor.monibotbaseapi.model.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chengfs on 2024/1/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceOnlineStats {

    private Long count;
    private Long online;
    private Long offline;
    private Integer monitorType;
}