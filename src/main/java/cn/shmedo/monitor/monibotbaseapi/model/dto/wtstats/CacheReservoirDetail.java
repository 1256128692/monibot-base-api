package cn.shmedo.monitor.monibotbaseapi.model.dto.wtstats;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2024-02-02 15:14
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CacheReservoirDetail {
    private Integer projectID;
    private String projectName;
    private String shortName;
    private String reservoirScale;
    private Double checkFloodWater;
    private Double designFloodWater;
    private Double normalStorageWater;
    private Double periodLimitWater;
    private Double totalCapacity;
    private String manageUnit;
    private String contactsPhone;
    private String administrationDirector;
    private String mainManagementDirector;
    private String managementDirector;
    private String patrolDirector;
    private String technicalDirector;
}
