package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Author wuxl
 * @Date 2024/5/9 10:20
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.response.sensor
 * @ClassName: MonitorTypeTemplateAndTemplateDataSource
 * @Description: TODO
 * @Version 1.0
 */
@Data
public class MonitorTypeTemplateAndTemplateDataSource {
    private Integer monitorTypeTemplateID;
    private String name;
    private Integer monitorType;
    private String templateDataSourceID;
    private String templateDataSourceToken;
}
