package cn.shmedo.monitor.monibotbaseapi.model.response.monitorItem;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-17 16:45
 */
@Data
@Builder(toBuilder = true)
public class MonitorTypeItemNameInfo {
    private Integer monitorTypeID;
    private String monitorTypeName;
    private Boolean multiSensor;
    @JsonIgnore
    private Integer displayOrder;
    private List<String> monitorItemNameList;
}
