package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-09-01 11:14
 */
@Data
public class VideoProjectViewBaseInfo {
    private Integer monitorGroupParentID;
    private String monitorGroupParentName;
    private Integer displayOrder;
    private List<VideoProjectViewSubGroupInfo> monitorGroupDataList;
}
