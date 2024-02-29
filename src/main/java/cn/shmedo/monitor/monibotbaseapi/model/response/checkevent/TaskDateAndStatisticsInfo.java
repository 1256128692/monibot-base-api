package cn.shmedo.monitor.monibotbaseapi.model.response.checkevent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDateAndStatisticsInfo {

    private List<TaskDataResponse> taskDataResponseList;

    private TaskStatusInfo taskStatusInfo;

    @Data
    public static class TaskStatusInfo {
        private Integer unpreparedCount;
        private Integer underwayCount;
        private Integer expiredCount;
        private Integer finishedCount;
    }

}
