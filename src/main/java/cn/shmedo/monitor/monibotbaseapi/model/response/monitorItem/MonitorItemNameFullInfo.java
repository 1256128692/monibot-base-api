package cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem;

import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-17 17:10
 */
@Data
public class MonitorItemNameFullInfo {
    private String monitorClassName;
    private Integer monitorTypeID;
    private String monitorTypeName;
    private String monitorItemName;
}
