package cn.shmedo.monitor.monibotbaseapi.model.dto.sensor;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbSensor;
import lombok.Data;

/**
 * @author Chengfs on 2023/11/29
 */
@Data
public class SensorSimple {

    private Integer id;
    private Integer projectID;
    private String name;
    private String alias;
    private Integer monitorType;
    private Integer kind;
    private Integer monitorPointID;

    public static SensorSimple valueOf(TbSensor entity) {
        SensorSimple result = new SensorSimple();
        result.setId(entity.getID());
        result.setProjectID(entity.getProjectID());
        result.setName(entity.getName());
        result.setAlias(entity.getAlias());
        result.setMonitorType(entity.getMonitorType());
        result.setKind(entity.getKind().intValue());
        result.setMonitorPointID(entity.getMonitorPointID());
        return result;
    }
}