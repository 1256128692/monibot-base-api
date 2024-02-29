package cn.shmedo.monitor.monibotbaseapi.model.response.checkevent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDataResponse {

    private Date taskDate;
    private List<TaskInfo> taskInfoList;

}
