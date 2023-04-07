package cn.shmedo.monitor.monibotbaseapi.model.param.third.iot;

import cn.shmedo.monitor.monibotbaseapi.service.third.iot.IotService;
import lombok.Data;

import java.util.Collection;

/**
 * {@link IotService#queryDeviceAndSensorList(QueryDeviceAndSensorRequest)}
 * @author Chengfs on 2023/4/6
 */
@Data
public class QueryDeviceAndSensorRequest {

    private Integer companyID;

    private Collection<Integer> deviceIDList;

    private Collection<String> deviceTokenList;

    private Collection<Integer> productIDList;

    private Collection<String> iotSensorTypeList;

    private Collection<Integer> sensorIDList;
}

    
    