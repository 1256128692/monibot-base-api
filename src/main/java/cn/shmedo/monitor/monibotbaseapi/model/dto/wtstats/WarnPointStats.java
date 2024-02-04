package cn.shmedo.monitor.monibotbaseapi.model.dto.wtstats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chengfs on 2024/1/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarnPointStats {

    private Integer projectID;
    private Integer level1;
    private Integer level2;
    private Integer level3;
    private Integer level4;
    private Integer offline;
    private Integer monitorType;
}