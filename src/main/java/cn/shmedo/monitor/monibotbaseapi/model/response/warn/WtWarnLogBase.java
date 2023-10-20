package cn.shmedo.monitor.monibotbaseapi.model.response.warn;

import cn.shmedo.monitor.monibotbaseapi.model.enums.WarnType;
import lombok.Data;

/**
 * @author Chengfs on 2023/10/20
 */
@Data
public class WtWarnLogBase {

    /**
     * 预警ID
     */
    private Integer warnID;
    /**
     * 预警名称
     */
    private String warnName;
    /**
     * 预警类型
     */
    private WarnType warnType;
    /**
     * 预警等级
     */
    private Integer warnLevel;
    /**
     * 预警时间
     */
    private String warnTime;
    /**
     * 项目ID
     */
    private Integer projectID;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 监测类型ID
     */
    private Integer monitorTypeID;
    /**
     * 监测类型名称
     */
    private String monitorTypeName;
    /**
     * 监测类型别名
     */
    private String monitorTypeAlias;
    /**
     * 监测项ID
     */
    private Integer monitorItemID;
    /**
     * 监测项名称
     */
    private String monitorItemName;
    /**
     * 监测点ID
     */
    private Integer monitorPointID;
    /**
     * 监测点名称
     */
    private String monitorPointName;


}