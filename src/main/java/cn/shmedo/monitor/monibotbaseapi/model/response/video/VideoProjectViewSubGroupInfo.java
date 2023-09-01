package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-09-01 11:18
 */
@Data
public class VideoProjectViewSubGroupInfo {
    private Integer monitorGroupID;
    private String monitorGroupName;
    private Integer displayOrder;
    private List<VideoProjectViewPointInfo> monitorPointDataList;
}
