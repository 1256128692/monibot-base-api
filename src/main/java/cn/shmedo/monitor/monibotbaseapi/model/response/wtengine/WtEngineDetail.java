package cn.shmedo.monitor.monibotbaseapi.model.response.wtengine;

import cn.shmedo.monitor.monibotbaseapi.model.db.TbWarnRule;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class WtEngineDetail {
    private Integer engineID;
    private Boolean enable;
    private String engineName;
    private String engineDesc;
    private Integer projectID;
    private String projectName;
    private Integer monitorItemID;
    private String monitorItemName;
    private String monitorItemAlias;
    private Integer monitorPointID;
    private String monitorPointName;
    private Date createTime;
    private Integer createUserID;
    private String createUserName;
    private List<WtWarnStatusDetailInfo> dataList;

    public static WtEngineDetail build(TbWarnRule tbWarnRule) {
        WtEngineDetail res = new WtEngineDetail();
        BeanUtils.copyProperties(tbWarnRule, res);
        return res.setEngineID(tbWarnRule.getID()).setEngineName(tbWarnRule.getName()).setEngineDesc(tbWarnRule.getDesc());
    }
}
