package cn.shmedo.monitor.monibotbaseapi.model.response.warn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;
import java.util.Set;

/**
 * @author Chengfs on 2023/5/5
 */
@Data
public class WtTerminalWarnDetailInfo {
    private Integer warnID;
    private String warnName;
    private Set<WtTerminalWarnLog.Project> projectList;
    private Integer warnLevel;
    private String warnContent;
    private Date warnTime;
    private String fieldToken;
    private String compareRule;
    private String triggerRule;
    private Integer workOrderID;
    private String workOrderCode;
    private String workOrderSolution;
    private String deviceToken;
    private String deviceTypeName;
    private String ruleName;
    @JsonIgnore
    private String uniqueToken;
}