package cn.shmedo.monitor.monibotbaseapi.model.param.third.iot;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 根据推送地址查询设备简单信息列表 参数
 *
 * @author Chengfs on 2023/4/28
 */
@Data
@Builder
public class QueryDeviceSimpleBySenderAddressParam {

    private Integer companyID;
    private Integer sendType;
    private List<String> sendAddressList;
    private Boolean sendEnable;
    private String deviceToken;

    private Boolean online;
    private Integer productID;
}