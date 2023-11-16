package cn.shmedo.monitor.monibotbaseapi.model.param.thematicDataAnalysis;

import lombok.Data;

import java.util.Date;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-16 10:03
 */
@Data
public class ManualDataItem {
    private Integer sensorID;
    private Date time;
    private String fieldToken;
    private String value;
}
