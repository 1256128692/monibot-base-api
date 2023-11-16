package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import lombok.Data;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-08 15:13
 */
@Data
public class ThematicGroupPointListInfo {
    private Integer monitorGroupID;
    private String monitorGroupName;
    private Boolean monitorGroupEnable;
    private List<ThematicPointListInfo> monitorPointDataList;
}
