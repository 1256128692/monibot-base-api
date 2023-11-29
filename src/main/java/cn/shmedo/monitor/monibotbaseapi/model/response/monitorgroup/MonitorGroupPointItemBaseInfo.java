package cn.shmedo.monitor.monibotbaseapi.model.response.monitorgroup;

import lombok.Data;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-27 14:06
 */
@Data
public class MonitorGroupPointItemBaseInfo {
    private Integer monitorPointID;
    private String monitorPointName;
    private Integer monitorItemID;
    private String monitorItemName;
    private String monitorItemAlias;
}