package cn.shmedo.monitor.monibotbaseapi.model.param.third.iot;

import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;
import java.util.List;

/**
 * {@link IotService#queryDeviceAndSensorList(QueryDeviceAndSensorRequest)}
 * @author Chengfs on 2023/4/6
 */
@Data
@Builder
public class QueryDeviceAndSensorRequest {

    private Integer companyID;

    private Collection<Integer> deviceIDList;

    private Collection<String> deviceTokenList;

    private Collection<Integer> productIDList;

    private Collection<String> iotSensorTypeList;

    private Collection<Integer> sensorIDList;

    private List<String> sendAddressList;

    private Integer sendType;
}

    
    