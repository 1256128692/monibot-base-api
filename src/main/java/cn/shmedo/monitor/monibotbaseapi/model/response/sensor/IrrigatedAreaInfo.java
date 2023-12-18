package cn.shmedo.monitor.monibotbaseapi.model.response.sensor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IrrigatedAreaInfo {

    private Date time;

    private Double avgWaterLevel;

    private Double avgWaterFlow;

    private Double waterTotal;
}
