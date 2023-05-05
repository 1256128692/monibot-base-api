package cn.shmedo.monitor.monibotbaseapi.model.response.warn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Set;

/**
 * @author Chengfs on 2023/5/5
 */
@Data
public class WtTerminalWarnLog {
    private Integer warnID;
    private Integer warnType;
    private String warnName;
    private Integer warnLevel;
    private Date warnTime;
    private String warnContent;
    private Set<Project> projectList;
    private Integer workOrderID;
    private String orderCode;
    private Integer orderStatus;
    private String deviceToken;
    private String deviceTypeName;
    @JsonIgnore
    private String uniqueToken;

    @Data
    public static class Project {
        private Integer projectID;
        private String projectName;
        private String regionArea;
        private Set<MonitorPoint> monitorPointList;
    }

    @Data
    @EqualsAndHashCode
    public static class MonitorPoint {
        private Integer monitorPointID;
        private String monitorPointName;
        private Integer monitorTypeID;
        private String monitorTypeName;
        private String monitorTypeAlias;
        private Integer monitorItemID;
        private String monitorItemName;
        private String monitorPointLocation;
        private String installLocation;
    }
}