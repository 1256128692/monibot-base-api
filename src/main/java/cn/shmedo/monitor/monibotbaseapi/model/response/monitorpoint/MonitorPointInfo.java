package cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint;

import lombok.Data;

/**
 * @Author wuxl
 * @Date 2024/5/8 15:46
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.response.monitorpoint
 * @ClassName: MonitorPointInfo
 * @Description: TODO
 * @Version 1.0
 */
@Data
public class MonitorPointInfo {
    private Integer ID;
    private Integer ProjectID;
    private Integer MonitorType;
    private Integer MonitorItemID;
    private String monitorPointName;
    private Integer monitorGroupID;
    private Integer monitorGroupParentID;
    private String monitorGroupName;
}
