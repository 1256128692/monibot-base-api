package cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup;

import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-17 18:13
 */
@Data
public class MonitorGroupPointBaseInfo {
    private Integer monitorGroupID;
    private String monitorGroupName;
    private List<MonitorGroupPointItemBaseInfo> monitorPointList;
}