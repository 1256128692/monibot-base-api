package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.Field;
import cn.shmedo.monitor.monibotbaseapi.model.dto.sensor.Param;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author Chengfs on 2023/4/10
 */
@Data
@AllArgsConstructor
public class TryingParamResponse {

    private Integer calType;

    private List<Field> fieldList;

    private String script;

    private List<Param> paramList;

    public TryingParamResponse() {
        this.fieldList = List.of();
        this.paramList = List.of();
    }

}

    
    