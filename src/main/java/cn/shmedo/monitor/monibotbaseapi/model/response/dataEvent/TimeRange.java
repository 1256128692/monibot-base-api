package cn.shmedo.monitor.monibotbaseapi.model.response.dataEvent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeRange {
    private Date startTime;
    private Date endTime;
}
