package cn.shmedo.monitor.monibotbaseapi.model.response.video;

import lombok.Data;

import java.util.List;

@Data
public class ProjectVideoInfo {

    private Integer projectID;

    private String projectName;

    private List<VideoBaseInfo> videoInfoList;
}
