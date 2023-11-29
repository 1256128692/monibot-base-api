package cn.shmedo.monitor.monibotbaseapi.model.response.thematicDataAnalysis;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author youxian.kong@shmedo.cn
 * @date 2023-11-16 15:02
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WetLineConfigInfo extends ThematicGroupPointListInfo {
    private String monitorGroupImagePath;
    private List<ThematicProjectConfigInfo> monitorGroupConfigList;
    private List<ThematicPointListInfoV2> monitorPointList;
    /**
     * just for {@code @JsonIgnore}
     */
    @JsonIgnore
    private List<ThematicPointListInfo> monitorPointDataList;
}
