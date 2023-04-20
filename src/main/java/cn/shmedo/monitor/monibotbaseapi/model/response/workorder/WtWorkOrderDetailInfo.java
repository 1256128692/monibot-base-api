package cn.shmedo.monitor.monibotbaseapi.model.response.workorder;

import lombok.Data;

import java.util.Date;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-20 11:46
 */
@Data
public class WtWorkOrderDetailInfo {
    private Integer workOrderID;
    private String orderCode;
    private Integer typeID;
    private String typeName;
    private Integer organizationID;
    private String organizationName;
    private String solution;
    private String dispatcherName;
    private Date dispatchTime;
    private Integer status;
}
