package cn.shmedo.monitor.monibotbaseapi.model.response.workorder;

import lombok.Data;

import java.util.Date;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-18 17:59
 */
@Data
public class WtWorkOrderInfo {
    private Integer workOrderID;
    private String orderCode;
    private Integer typeID;
    private String typeName;
    private Integer organizationID;
    private String organizationName;
    private String solution;
    private String dispatcherName;
    private Date dispatchTime;
    private String disposerName;
    private Date disposeTime;
    private Integer status;
}
