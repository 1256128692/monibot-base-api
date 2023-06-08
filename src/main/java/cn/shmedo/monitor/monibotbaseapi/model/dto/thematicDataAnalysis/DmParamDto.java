package cn.shmedo.monitor.monibotbaseapi.model.dto.thematicDataAnalysis;

import cn.shmedo.monitor.monibotbaseapi.model.response.projectconfig.ConfigBaseResponse;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-29 10:44
 */
@Data
@Builder
public class DmParamDto {
    private Integer monitorPointID;
    private String monitorPointName;
    private Integer monitorType;
    private Map<Integer, ConfigBaseResponse> sensorIDConfigMap;
}
