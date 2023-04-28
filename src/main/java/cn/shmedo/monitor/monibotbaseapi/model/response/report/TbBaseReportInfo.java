package cn.shmedo.monitor.monibotbaseapi.model.response.report;

import lombok.Data;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-04-28 09:37
 */
@Data
public class TbBaseReportInfo {
    private String monitorPointName;
    private String projectName;
    private Integer status;
    private String monitorTypeName;
    private String monitorTypeClass;
    private String monitorItemName;
    private String areaCode;
}
