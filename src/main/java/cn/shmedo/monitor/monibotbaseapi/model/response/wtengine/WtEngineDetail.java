package cn.shmedo.monitor.monibotbaseapi.model.response.wtengine;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class WtEngineDetail {
    private Integer engineID;
    private Integer ruleType;
    private Boolean enable;
    private String engineName;
    private String engineDesc;
    private Integer projectID;
    private String projectName;
    private Integer monitorItemID;
    private String monitorItemName;
    private String monitorItemAlias;
    private Integer monitorTypeID;
    private String monitorTypeName;
    private String monitorTypeAlias;
    private Integer monitorPointID;
    private String monitorPointName;
    private Date createTime;
    private Integer createUserID;
    private String createUserName;
    private String videoType;
    private Integer productID;
    private String productName;
    private String deviceCSV;
    private String videoCSV;
    private List<WtWarnStatusDetailInfo> dataList;
}
