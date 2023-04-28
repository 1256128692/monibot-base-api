package cn.shmedo.monitor.monibotbaseapi.model.response.report;

import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-28 09:37
 */
@Data
public class TbBaseReportInfo {
    private Integer sensorID;
    private String monitorPointName;
    private String projectName;
    private Integer status;
    private Integer monitorType;
    private String monitorTypeName;
    private String monitorTypeClass;
    private String monitorItemName;
    private String areaCode;
    // {columnName:fieldToken}, e.g.{水库库容(m):capacity},{水位(m):distance}
    private String customColumn;
    private String upperName;
}
