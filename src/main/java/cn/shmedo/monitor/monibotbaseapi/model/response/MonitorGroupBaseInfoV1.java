package cn.shmedo.monitor.monibotbaseapi.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitorGroupBaseInfoV1 {

    private Integer groupID;
    private Integer parentID;
    private String groupName;
    private Boolean enable;

    private List<MonitorGroupBaseInfoV1> childGroupList;

}
