package cn.shmedo.monitor.monibotbaseapi.model.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Chengfs on 2024/1/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceOnlineStatsResponse {

    private Long count;

    private Long online;

    private Long offline;

    private List<MonitorType> monitorType;

    public record MonitorType(Integer monitorType, String typeName, Long count, Long online, Long offline) {

    }
}