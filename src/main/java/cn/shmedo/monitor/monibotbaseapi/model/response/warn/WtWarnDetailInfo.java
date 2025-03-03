package cn.shmedo.monitor.monibotbaseapi.model.response.warn;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnAction;
import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnType;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IFieldToken;
import cn.shmedo.monitor.monibotbaseapi.model.standard.IMonitorType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-14 17:13
 */
@Data
public class WtWarnDetailInfo implements IFieldToken, IMonitorType {
    private Integer warnID;
    private WarnType warnType;
    private String warnName;
    private Integer projectID;
    private String projectName;
    private Integer monitorTypeID;
    @JsonIgnore
    private Integer monitorType;
    private String monitorTypeName;
    private String monitorTypeAlias;
    private Integer monitorItemID;
    private String monitorItemName;
    private Integer warnLevel;
    private String warnContent;
    private Integer monitorPointID;
    private String monitorPointName;
    private String monitorPointLocation;
    private String installLocation;
    private Date warnTime;
    private String fieldToken;
    private String compareRule;
    private String triggerRule;
    private Integer workOrderID;
    private String workOrderCode;
    private String workOrderExValue;
    private String workOrderSolution;
    private String deviceToken;
    private String deviceTypeName;
    private String regionArea;
    private String ruleName;
    private List<TbWarnAction> actionList;

    //暴雨预警 warnType: 6
    //预警发布单位
    private String issueUnit;
    //预警区域行政区划码
    private Integer warnAreaCode;
    //预警区域名称
    private String warnArea;
    //预警区域经纬度
    private String warnAreaLocation;
}
