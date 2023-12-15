package cn.shmedo.monitor.monibotbaseapi.model.dto.watermeasure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Chengfs on 2023/12/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WaterMeasurePointInfo extends WaterMeasurePointSimple {

    private Integer monitorItemID;

    private String monitorItemName;

    private String gpsLocation;

}