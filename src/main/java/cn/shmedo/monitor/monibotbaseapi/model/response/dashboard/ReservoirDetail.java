package cn.shmedo.monitor.monibotbaseapi.model.response.dashboard;

import lombok.Builder;
import lombok.Data;

/**
 * 水库项目详情实体
 *
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2024-01-31 16:54
 **/
@Data
@Builder
public class ReservoirDetail {
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
