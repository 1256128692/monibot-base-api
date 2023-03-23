package cn.shmedo.monitor.monibotbaseapi.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitorPointTypeStatisticsInfo {

    private List<MonitorTypeBaseInfo> typeInfoList;


}
